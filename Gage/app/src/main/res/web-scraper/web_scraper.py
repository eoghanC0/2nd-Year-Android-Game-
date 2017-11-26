import urllib.request as getImage
import json

rawData = json.load(open('raw_players.json'))

for counter in range(0, len(rawData["Players"])) :
    id = rawData["Players"][counter]["id"]
    try :
        resource = getImage.urlretrieve("https://fifa17.content.easports.com/fifa/fltOnlineAssets/CC8267B6-0817-4842-BB6A-A20F88B05418/2017/fut/items/images/players/html5/120x120/" + str(id) + ".png", "../../assets/img/playerBitmaps/" + str(id) + ".png")
    except :
        print("404 Error : ID - " + str(id))