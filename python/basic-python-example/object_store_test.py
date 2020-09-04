#! /usr/bin/env python

import boto3, sys, getopt, uuid
from botocore.exceptions import ClientError

session = boto3.session.Session()

s3_client = session.client(
    service_name='s3',
    endpoint_url='https://stratus.ucar.edu',
)

put_file=False
get_file=False
delete_file=False
list_buckets=False
list_objects=False
bucket=''
file_name=''

try:
    options, operands = getopt.getopt(sys.argv[1:], 'hb:pgdtl', ['help', 'bucket=', '--put', '--get', '--delete', '--listbuckets', '--listobjects'])
    for opt, arg in options:
        if opt in ('-h', '--help'):
            print('-----')
            print('Usage:')
            print('  python3 object_store_test.py [OPTIONS] [FILE...]')
            print('Options:')
            print('  -h, --help')
            print('    show help usage message')
            print('  -p, --put') 
            print('    file specified will be put on object store')
            print('  -g, --get')
            print('    object specified will be downloaded from object store')
            print('  -d, --delete')
            print('    object specified will be deleted from object store')
            print('  -b, --bucket')
            print('    bucket name')
            print('  -t, --listbuckets')
            print('    list buckets')
            print('  -l, --listobjects')
            print('    list objects')
            print('-----')
            sys.exit(1)
        elif opt in ('-b', '--bucket'): bucket = arg
        elif opt in ('-p', '--put'): put_file = True
        elif opt in ('-g', '--get'): get_file = True
        elif opt in ('-d', '--delete'): delete_file = True
        elif opt in ('-t', '--listbuckets'): list_buckets = True
        elif opt in ('-l', '--listobjects'): list_objects = True
        else: assert False, 'Unhandled Option'
    for f in operands:
        file_name = f
except getopt.GetoptError:
    print('Incorrect Usage')
    print('For help, run:')
    print('  object_store_test.py -h')
    sys.exit(1)

#generate random object name for better performance
random_file_name = ''.join([str(uuid.uuid4().hex[:6]), file_name])
object_name = random_file_name

if list_buckets:
    print("BUCKETS")
    print(s3_client.list_buckets())
    print("")

if list_objects:
    if bucket:
        print("Objects in " + bucket + " bucket")
        response = s3_client.list_objects_v2(Bucket=bucket)
        for obj in response['Contents']:
            print(obj)
        print("")
    else:
        print("Need to provide a bucket name")

if put_file:
    print("Generated random object name:")
    print(object_name)
    try:
        response = s3_client.upload_file(Filename=file_name, Bucket=bucket, Key=object_name)
    except ClientError as e:
        logging.error(e)
elif get_file:
    try:
        s3_client.download_file(Bucket=bucket, Key=file_name, Filename=file_name)
    except ClientError as e:
        logging.error(e)
elif delete_file:
    try:
        response = s3_client.delete_object(Bucket=bucket, Key=file_name)
    except ClientError as e:
        logging.error(e)
