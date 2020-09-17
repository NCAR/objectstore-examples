//groovysh:
/*
groovy.grape.Grape.grab([group:'org.slf4j',module:'slf4j-simple',version:'1.7.30',,transitive:true])
groovy.grape.Grape.grab([group:'software.amazon.awssdk',module:'s3',version:'2.14.9',transitive:true])
groovy.grape.Grape.grab([group:'com.twmacinta',module:'fast-md5',version:'2.7.1',,transitive:true])
*/

// file.groovy:
@Grapes([
    @Grab(group='org.slf4j', module='slf4j-simple', version='1.7.30'),
    @Grab(group='software.amazon.awssdk', module='s3', version='2.14.9'),
    @Grab('com.twmacinta:fast-md5:2.7.1'),
    ])

import java.nio.file.Paths;
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.utils.Md5Utils
import software.amazon.awssdk.core.sync.ResponseTransformer;

endpoint = 'https://stratus.ucar.edu'.toURI()

if (args.length != 2) {
  System.err.println 'Usage: groovy get bucket key'
  System.exit(1)
  }
bucketName = args[0]
keyName = args[1]

s3 = S3Client.builder().endpointOverride(endpoint).build()

s3.getObject(GetObjectRequest.builder().bucket(bucketName).key(keyName).build(),
    ResponseTransformer.toFile(Paths.get(keyName)));

println "Retrieved file: " + keyName;
