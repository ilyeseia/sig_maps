package dz.eadn.sig.model;

public enum FieldType {
	TEXT("String"), NUMBER("Double"),INTEGER("Integer"), IMAGE("String"), TIME("java.lang.String"), DATE("java.lang.String"),
	DATETIME("java.lang.String"), SELECT("String"), MULTI_SELECT("String"), BOOLEAN("Boolean"), TEXTAREA("String"),
	CAROUSEL("String"), HTMLEDITOR("String");
	

	private String javaClass;

	private FieldType(String javaClass) {
		this.javaClass = javaClass;
	}

	public String getJavaClass() {
		return javaClass;
	}
}
