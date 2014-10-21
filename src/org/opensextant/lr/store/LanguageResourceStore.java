package org.opensextant.lr.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvector.TermVectorRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.opensextant.lr.Common;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.lr.resources.LanguageResource;
import org.opensextant.lr.resources.LanguageResourceGroup;


@SuppressWarnings("unused")
public class LanguageResourceStore {

	Client client;

	ObjectMapper m = new ObjectMapper();

	public LanguageResourceStore(String host, int port) {

		client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(host, port));
		// build structure if not present
		prepareIndex();
	}

	public LanguageResource getResource(String word, LRTYPE type) {

		List<LanguageResource> lrs = this.query(word, type);
		if (lrs.size() == 0) {
			return null;
		}

		if (lrs.size() == 1) {
			return lrs.get(0);
		}

		// warning for multiple responses
		return lrs.get(0);

	}

	public LanguageResourceGroup getResourceGroup(String word) {

		List<LanguageResource> lrs = this.query(word, null);

		if (lrs.size() > 0) {
			LanguageResourceGroup grp = new LanguageResourceGroup();
			for (LanguageResource lr : lrs) {
				grp.addResources(lr);
			}
			return grp;
		}
		return new LanguageResourceGroup();
	}

	public List<LanguageResource> getAllLanguageResources(LRTYPE type) {

		List<LanguageResource> lrs = this.query(null, type);

		return lrs;
	}

	public void save(LanguageResource lr) {

		IndexRequestBuilder reqBuilder = client.prepareIndex(Common.LRNAME,
				lr.getStoreType().name()).setId(lr.getBaseForm());

		String json = LRtoJSON(lr);

		IndexResponse zz = reqBuilder.setSource(json).execute().actionGet();

	}

	
	
	public void save(List<LanguageResource> lrs) {
		BulkRequestBuilder bulk = client.prepareBulk();

		for(LanguageResource lr :lrs){
			String json = LRtoJSON(lr);
			bulk.add(client.prepareIndex(Common.LRNAME,lr.getStoreType().name()).setId(lr.getBaseForm()).setSource(json));
		}
		bulk.execute().actionGet();
	}
	
	
	public void flush(){
		
		FlushRequest flReq = new FlushRequest();
		flReq.indices(Common.LRNAME);
		flReq.full();
		client.admin().indices().flush(flReq ).actionGet();
		
		RefreshRequest refReq = new RefreshRequest();
		refReq.indices(Common.LRNAME);
		refReq.force();
		client.admin().indices().refresh(refReq ).actionGet();
		
	}
	
	
	private void prepareIndex() {
		boolean indexExists = client.admin().indices().prepareExists(Common.LRNAME).execute().actionGet().isExists();

		if (!indexExists) {
			String[] mapping = new String[2];
			mapping[0] = Common.KEYFIELD;
			mapping[1] = "type=string,index=not_analyzed";
			
			CreateIndexRequestBuilder indexReq = client.admin().indices().prepareCreate(Common.LRNAME);
			
			for(LRTYPE type :LRTYPE.values()){
				indexReq.addMapping(type.name(), mapping);
				
			}
			
			indexReq.execute().actionGet();
		}

	}

	private List<LanguageResource> query(String word, LRTYPE type) {

		List<LanguageResource> lrList = new ArrayList<LanguageResource>();

		int pageSize = 1000;

		SearchRequestBuilder s = client.prepareSearch(Common.LRNAME).setSize(
				pageSize);

		if (type != null) {
			s.setTypes(type.name());
		}

		if (word != null) {
			TermFilterBuilder wrdFilter = FilterBuilders.termFilter(Common.KEYFIELD, word);
			TermQueryBuilder wrdQuery = QueryBuilders.termQuery(Common.KEYFIELD, word);
			s.setQuery(QueryBuilders.filteredQuery(wrdQuery, wrdFilter));
		//	s.setQuery(QueryBuilders.termQuery(Common.KEYFIELD, word));
		//	s.setQuery(QueryBuilders.matchQuery(Common.KEYFIELD, word));
		//	s.setQuery(QueryBuilders.matchPhraseQuery(Common.KEYFIELD, word));
			
		} else {
			s.setQuery(QueryBuilders.matchAllQuery());
		}

		
		
		int returnCount = 1;
		int page = 0;
		while (returnCount > 0) {
			s.setFrom(page * pageSize);
			SearchResponse resp = s.execute().actionGet();

			SearchHits hits = resp.getHits();
			SearchHit[] hitArray = hits.getHits();

			returnCount = hitArray.length;
			for (SearchHit hit : hitArray) {
				String h = hit.getSourceAsString();
				LanguageResource lr = jsonToLR(h);
				lrList.add(lr);
			}
			page++;

		}
		return lrList;

	}

	// TODO ?
	public LanguageResourceGroup tag(String index, String type, String field, String text) {
		
		return null;
	}
	
	public Set<String> terms(){
		
		Set<String> terms = new HashSet<String>();
		List<LanguageResource> lrs = query(null,null);
		
		for(LanguageResource lr : lrs){
			terms.add(lr.getBaseForm());
		}
		
		return terms;
	}
	
	private LanguageResource jsonToLR(String json) {

		HashMap<?, ?> map = null;
		LanguageResource lr = null;
		try {
			map = m.readValue(json, HashMap.class);
			Map fields = (Map) map.get("fields");
			String typ = (String) fields.get("storeType");

			Class<? extends LanguageResource> cl = LRTYPE.valueOf(typ).getClazz();
			lr = m.readValue(json, cl);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lr;

	}

	private String LRtoJSON(LanguageResource lr) {

		String json = "";
		try {
			json = m.writeValueAsString(lr);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

}
