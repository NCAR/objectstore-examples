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
import software.amazon.awssdk.utils.Md5Utils

endpoint = 'https://stratus.ucar.edu'.toURI()

if (args.length != 3) {
  System.err.println 'Usage: groovy put bucket key file'
  System.exit(1)
  }
bucketName = args[0]
keyName = args[1]
fileName = args[2]

file = new File(fileName)
path=java.nio.file.Paths.get(file.absolutePath)

// duplicate computation for demo
// beware: Md5Utils.md5AsBase64(byte[]) computes the hash for the bytes, it does not convert the output of computeMD5Hash() into Base64!
digestBytes=Md5Utils.computeMD5Hash(file)
digestHexStr=digestBytes.encodeHex().toString()
digestB64=digestBytes.encodeBase64().toString()
digestBase64=Md5Utils.md5AsBase64(file)
println "file $file has MD5 $digestHexStr => $digestBase64"
assert digestB64==digestBase64

s3 = S3Client.builder().endpointOverride(endpoint).build()

putobj = PutObjectRequest.builder().bucket(bucketName).
 contentLength(file.size()).
 contentType(URLConnection.fileNameMap.getContentTypeFor(file.name)).
 contentMD5(digestBase64).
 key(keyName).
 build()

// max 5G

response = s3.putObject(putobj,path)
println response

// we have to provide a Base64 hash but the returned value is hex
// and contains " in the String: '"55a6a417ac72a6e9911688b47251c187"'
assert response.eTag()[1..-2]==digestHexStr
