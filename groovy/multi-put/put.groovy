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

import java.time.*
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.utils.Md5Utils
import software.amazon.awssdk.core.sync.RequestBody
import com.twmacinta.util.MD5
import com.twmacinta.util.MD5InputStream

com.twmacinta.util.MD5.initNativeLibrary(true)  // true: disable MD5.so; it's supposed to be faster but doesn't seem so

endpoint = 'https://stratus.ucar.edu'.toURI()

if (args.length < 3) {
  System.err.println 'Usage: groovy put bucket key file [md5Hex md5Base64]'
  System.exit(1)
  }
bucketName = args[0]
keyName = args[1]
fileName = args[2]
digestHexStr=null
digestB64=null
if (args.length==5) {
  digestHexStr = args[3]
  digestB64 = args[4]
  }

println "bucket $bucketName\nkey $keyName\nfile $fileName"

file = new File(fileName)
path=java.nio.file.Paths.get(file.absolutePath)

// duplicate computation for demo
// beware: Md5Utils.md5AsBase64(byte[]) computes the hash for the bytes, it does not convert the output of computeMD5Hash() into Base64!

if (!digestHexStr) {
    println "hashing..."
    startInstant=Instant.now()
    digestBytes=MD5.getHash(file) // fast!
    //digestBytes=Md5Utils.computeMD5Hash(file) // slow!
    endInstant=Instant.now()
    dur=Duration.between(startInstant,endInstant)
    dursec = dur.seconds + dur.nano/1000000000
    println "hashing complete after $dursec seconds"
    digestHexStr=digestBytes.encodeHex().toString()
    digestB64=digestBytes.encodeBase64().toString()
    //digestBase64=Md5Utils.md5AsBase64(file) // slow!
}
println "file $file has MD5 $digestHexStr => $digestB64"

s3 = S3Client.builder().endpointOverride(endpoint).build()

createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
        .bucket(bucketName).key(keyName)
        .build();
response = s3.createMultipartUpload(createMultipartUploadRequest)
uploadId = response.uploadId()
createMultipartUploadRequest = null
println "uploadId = $uploadId"

completedParts = []
partSize = 5L*1024*1024*1024 // 5GB
println "partSize = $partSize"
fileSize = file.size()
println "fileSize = $fileSize"

println ''

uploadPartRequest = UploadPartRequest.builder().bucket(bucketName).key(keyName)
        .uploadId(uploadId)
        .partNumber(0).build();
startByte=0L
partNumber=1
while (startByte<fileSize) {
    currPartSize=partSize
    if (startByte+currPartSize > fileSize) currPartSize = fileSize-startByte
    println "part $partNumber start $startByte size $currPartSize"

    // use MD5InputStream to get hash of each part
    // aws-sdk has ChecksumCalculatingInputStream with reset() but it's slower
    // BufferedInputStream is much slower
    is = new MD5InputStream(new FileInputStream(file))
    is.skip(startByte)

    startInstant=Instant.now()
    uploadPartRequest = uploadPartRequest.toBuilder().partNumber(partNumber).build()
    reqBody = RequestBody.fromInputStream(is,currPartSize)
    etag = s3.uploadPart(uploadPartRequest,reqBody).eTag();
    println "etag = $etag"
    parthash = is.MD5.asHex()
    println "part MD5 = $parthash"
    if (parthash == etag[1..-2])    // strip quotes '"etag"'
      println "MD5==etag"
    else println "part MD5 $parthash != returned etag $etag"
    completedParts << CompletedPart.builder().partNumber(partNumber).eTag(etag).build()

    endInstant=Instant.now()
    dur=Duration.between(startInstant,endInstant)
    dursec = dur.seconds + dur.nano/1000000000
    println "part $partNumber complete after $dursec seconds"
    println ''

    startByte+=currPartSize
    partNumber++
}
uploadPartRequest = null
is.close()
is=null

completedMultipartUpload = CompletedMultipartUpload.builder().
    parts(completedParts).
    build()
completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder().
    bucket(bucketName).key(keyName).uploadId(uploadId).
    multipartUpload(completedMultipartUpload).
    build()
startInstant=Instant.now()
response = s3.completeMultipartUpload(completeMultipartUploadRequest)
endInstant=Instant.now()
dur=Duration.between(startInstant,endInstant)
dursec = dur.seconds + dur.nano/1000000000
println "completeMultipartUpload after $dursec seconds"
println response

completedMultipartUpload = null
completeMultipartUploadRequest = null
