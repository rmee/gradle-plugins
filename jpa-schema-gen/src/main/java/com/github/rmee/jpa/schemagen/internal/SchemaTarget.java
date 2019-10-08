package com.github.rmee.jpa.schemagen.internal;

import com.github.rmee.jpa.schemagen.SchemaGenConfig;

import java.io.File;

public interface SchemaTarget {

	void process(File generatedFile, File outputFile, SchemaGenConfig config);
}
