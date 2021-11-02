from ShazamAPI import Shazam
import json
import sys


mp3_file_content_to_recognize = open(sys.argv[1], 'rb').read()
shazam = Shazam(mp3_file_content_to_recognize)
recognize_generator = shazam.recognizeSong()
json_obj = json.loads(json.dumps((next(recognize_generator))))
del json_obj[0]
json_obj = json_obj[0]
print(json.loads(json_obj))
