package org.opensextant.lr.resources;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.opensextant.lr.Common.LRTYPE;
import org.opensextant.placedata.Place;

public class LRPlace extends LanguageResource {

	static String placeFieldName = "place";
	static LRTYPE storeType = LRTYPE.place;

	private Place place;

	public LRPlace() {
		this.setField(storeTypeFieldName, storeType);
	}

	public LRPlace(String name) {
		super(name);
		this.setField(storeTypeFieldName, storeType);
		this.place = new Place();
		this.place.setPlaceName(name);
	}
	@JsonIgnore
	public Place getPlace() {
		return (Place) this.getField(placeFieldName);
		
	}

	public void setPlace(Place place) {
		this.setField(placeFieldName, place);
		
	}

	@Override
	public LRTYPE getStoreType() {
		return storeType;
	}

}
