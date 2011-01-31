package com.mostka.phprpc.linker;


import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.linker.Artifact;

public class PhpRpcDataArtifact extends Artifact<PhpRpcDataArtifact> {
	private final String phpRpcClassName = "phpRpcClassName";
	private final Map<String, Integer> fieldsByClassName = new HashMap<String, Integer>();
	
	public PhpRpcDataArtifact() {
		super(PhpRpcLinker.class);	
	}

	@Override
	public int hashCode() {
		return phpRpcClassName.hashCode();
	}
	public void addClassName(String className) {
	    fieldsByClassName.put(className, 0);
	}
	@Override
	protected int compareToComparableArtifact(PhpRpcDataArtifact o) {
		return phpRpcClassName.compareTo(o.phpRpcClassName);
	}

	@Override
	protected Class<PhpRpcDataArtifact> getComparableArtifactType() {
		return PhpRpcDataArtifact.class;
	}

}
