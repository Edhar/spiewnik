package com.zelwise.spiewnik;

import java.util.ArrayList;

public class Language {
	public static final String Tag = "language";
	
	public static final ArrayList<Language> GetLanguages(){
		ArrayList<Language> list = new ArrayList<Language>();
		list.add(new Language(0,"Polski","pl"));
		list.add(new Language(1,"English","en"));
		return list;
	}
	
	public static final Language Get(Integer Id){
		for (Language lang : GetLanguages()) {
			if(lang.Id() == Id){
				return lang;
			}
		}
		
		return new Language();
	}
	
	private Integer id = -1;
	public Integer Id(){
		return id;
	}
	
	private String name = "";
	public String Name(){
		return name;
	}
	
	private String languageCode = "";
	public String LanguageCode(){
		return languageCode;
	}
	
	public Language(){
		
	}
	
	public Language(Integer id,String name, String languageCode){
		this.id = id;
		this.name = name;
		this.languageCode = languageCode;
	}
	
	@Override
	public String toString() {
		return this.Name();
	}
}
