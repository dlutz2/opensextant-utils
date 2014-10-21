package org.opensextant.tagger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonRawValue;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;

public class ElasticDocument implements Streamable {

	static ObjectMapper m = new ObjectMapper();

	private String id;
	@JsonRawValue
	@JsonIgnore
	private String contents;

	private Map<String, Object> fields = new HashMap<String, Object>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContents() {

		if (this.contents != null) {
			return contents;
		}

		try {
			String tmpContents = m.writeValueAsString(this.fields);
			return tmpContents;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public void setContents(String contents) {
		this.contents = contents;
		populateFields();
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public Object getField(String fieldName) {
		return fields.get(fieldName);
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	public void setField(String fieldName, Object value) {
		this.fields.put(fieldName, value);
	}

	private void populateFields() {

		if (this.contents == null) {
			this.fields = new HashMap<String, Object>();
			return;
		}

		try {
			this.fields = m.readValue(this.contents, HashMap.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("ID=" + this.id);
		buf.append(" ");
		buf.append("Contents=" + this.contents);
		return buf.toString();
	}

	@Override
	public void readFrom(StreamInput in) throws IOException {
		this.id = in.readString();
		this.contents = in.readString();

	}

	@Override
	public void writeTo(StreamOutput out) throws IOException {
		out.writeString(this.id);
		out.writeString(this.contents);
	}

}
