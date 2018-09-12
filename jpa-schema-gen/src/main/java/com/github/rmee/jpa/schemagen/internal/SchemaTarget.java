package com.github.rmee.jpa.schemagen.internal;

import java.io.File;

import com.github.rmee.jpa.schemagen.SchemaGenExtension;

public interface SchemaTarget {

	void process(File generatedFile, File outputFile, SchemaGenExtension config);
}
