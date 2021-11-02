from ShazamAPI import Shazam
import json
import sys

string = ""

for i in range(1, len(sys.argv)):
    string = string + sys.argv[i] + " "
string = string[:-1]

mp3_file_content_to_recognize = open(string, 'rb').read()
shazam = Shazam(mp3_file_content_to_recognize)
recognize_generator = shazam.recognizeSong()
json_obj = json.loads(json.dumps((next(recognize_generator))))
del json_obj[0]
json_obj = json_obj[0]
print(json_obj)
