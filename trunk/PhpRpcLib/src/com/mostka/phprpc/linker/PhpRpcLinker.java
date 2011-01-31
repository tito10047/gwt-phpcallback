package com.mostka.phprpc.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;

@LinkerOrder(Order.POST)
@Shardable
public class PhpRpcLinker extends AbstractLinker {
	private static final String SUFFIX = ".gwt.phprpc";
	
	@Override
	public String getDescription() {
		System.out.println("PhpRpcLinker - onePermutation 000");
		return "dePHPRPC linker";
	}
	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation){
		if (onePermutation) {
			System.out.println("PhpRpcLinker - onePermutation true");
		}else{
			System.out.println("PhpRpcLinker - onePermutation false");
		}
		
		return artifacts;
	}
}
