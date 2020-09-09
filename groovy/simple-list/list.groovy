//groovysh:
// groovy.grape.Grape.grab([group:'org.slf4j',module:'slf4j-simple',version:'1.7.30',,transitive:true])
// groovy.grape.Grape.grab([group:'software.amazon.awssdk',module:'aws-sdk-java',version:'2.14.9',transitive:true])

// file.groovy:
@Grapes([
    @Grab(group='org.slf4j', module='slf4j-simple', version='1.7.30'),

    // grapes can't handle bom without a JAR, workaround with redundant spec for .pom
    @Grab(group='software.amazon.awssdk', module='bom', version='2.14.9', type='pom'),
    @Grab(group='software.amazon.awssdk', module='s3', version='2.14.9'),

    // or we can get it all (big and slow!)
    //@Grab('software.amazon.awssdk:aws-sdk-java:2.14.9'),

    ])

import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.S3Client

endpoint = 'https://stratus.ucar.edu'.toURI()
bucketName = args.length ? args[0] : 'eol-codiac'

s3 = S3Client.builder().endpointOverride(endpoint).build()

// from https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/javav2/example_code/s3/src/main/java/com/example/s3/ListObjects.java
println "v1 list $bucketName:\n"
listObjects = ListObjectsRequest.builder().bucket(bucketName).build()
response = s3.listObjects(listObjects)
objects = response.contents()
objects.eachWithIndex { it, i ->
 println "$i isa ${it.getClass()}"
 println it
 println "key = ${it.key}"
 println "size = ${it.size}"
 println ''
}

println "\n\n"

println "v2 list $bucketName:\n"
listObjects = ListObjectsV2Request.builder().bucket(bucketName).build()
response = s3.listObjectsV2(listObjects)
objects = response.contents()
objects.eachWithIndex { it, i ->
 println "$i isa ${it.getClass()}"
 println it
 println "key = ${it.key}"
 println "size = ${it.size}"
 println "size via value = " + it.getValueForField('Size',Object).get()
 println ''
}

