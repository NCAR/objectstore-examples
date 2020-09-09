# groovy objectstore examples

Make sure you have Groovy installed, any version that
works with your Java. Groovy 2.5+ or 3.x and OpenJDK 1.8
are known to work well. Set your `JAVA_HOME` environment
variable.

We use the Java libraries for many examples.

 * [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
   * aws-sdk-java v1 [src](https://github.com/aws/aws-sdk-java)
   * aws-sdk-java v2 [src](https://github.com/aws/aws-sdk-java-v2)
   [doc](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/welcome.html)
   [api](https://sdk.amazonaws.com/java/api/latest/)

Groovy [Grapes](http://groovy-lang.org/grape.html) does not do well
with the bill-of-materials dependency since it does not have a JAR file.
One can grab the entire SDK or workaround the bom by redundantly
specifying the `.pom` file.

    @Grab(group='software.amazon.awssdk', module='bom', version='2.14.9', type='pom'),
    @Grab(group='software.amazon.awssdk', module='s3', version='2.14.9'),

The full SDK is very large and slow to download initially, and even the
BOM is slow to verify on each subsequent run. We suggest that your grapes cache
directory be on a suitably large and fastish disk.
You will probably also need to add the modern Maven repos to your
config (e.g. `~/.groovy/grapeConfig.xml`), and be sure to have bintray:

    <ibiblio name="mavenApache" root="https://repo.maven.apache.org/maven2/" m2compatible="true"/>
    <ibiblio name="mavenCentral" root="https://repo1.maven.org/maven2/" m2compatible="true"/>
    <ibiblio name="bintray" root="https://jcenter.bintray.com/" m2compatible="true"/>

