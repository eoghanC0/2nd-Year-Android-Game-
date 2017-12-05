import urllib.request
import json
import os.path

urlString = "https://www.easports.com/fifa/ultimate-team/api/fut/item?page="
outputFile = "../res/player_json/all_cards.json"
pageNumber = 0
derivedData = {"players": []}

while True:
    pageNumber += 1
    print(str(pageNumber) + "/666")
    with urllib.request.urlopen(urlString + str(pageNumber)) as url:
        rawData = json.loads(url.read().decode())

    for counter in range(0, len(rawData["items"])):
        rawPlayer = rawData["items"][counter]
        if rawPlayer["league"]["abbrName"] == "ENG 1" and \
                (rawPlayer["color"] == "gold" or rawPlayer["color"] == "rare_gold" or
                    rawPlayer["color"] == "silver" or rawPlayer["color"] == "rare_silver" or
                    rawPlayer["color"] == "bronze" or rawPlayer["color"] == "rare_bronze"):

            newID = str(len(derivedData["players"]))

            if rawPlayer["position"] == "ST" or rawPlayer["position"] == "CF" or rawPlayer["position"] == "LW" \
                    or rawPlayer["position"] == "RW":
                position = "Forward"
            if rawPlayer["position"] == "LM" or rawPlayer["position"] == "RM" or rawPlayer["position"] == "CAM" \
                    or rawPlayer["position"] == "CM" or rawPlayer["position"] == "CDM":
                position = "Midfield"
            if rawPlayer["position"] == "LWB" or rawPlayer["position"] == "RWB" or rawPlayer["position"] == "CB" \
                    or rawPlayer["position"] == "LB" or rawPlayer["position"] == "RB":
                position = "Defence"
            if rawPlayer["position"] == "GK":
                position = "GoalKeeper"

            attributes = []
            for i in range(0,len(rawPlayer["attributes"])):
                attName = rawPlayer["attributes"][i]["name"][-3:]
                if attName == "PHY":
                    attName = "HEA"
                newAttribute = {"name": attName, "value": rawPlayer["attributes"][i]["value"]}
                attributes.append(newAttribute)

            if "rare" in rawPlayer["color"]:
                rare = True
            else:
                rare = False

            newPlayer = {"id": newID, "displayName": rawPlayer["commonName"], "firstName": rawPlayer["firstName"],
                         "lastName": rawPlayer["lastName"], "headshotBitmap": newID + ".png",
                         "league": {"abbrName": rawPlayer["league"]["abbrName"], "name": rawPlayer["league"]["name"]},
                         "club": {"abbrName": rawPlayer["club"]["abbrName"], "name": rawPlayer["club"]["name"],
                                  "logo": rawPlayer["club"]["abbrName"] + ".png"},
                         "nation": {"abbrName": rawPlayer["nation"]["abbrName"], "name": rawPlayer["nation"]["name"],
                                    "logo": rawPlayer["nation"]["abbrName"] + ".png"},
                         "position": position,
                         "attributes": attributes,
                         "rating": rawPlayer["rating"],
                         "rare": rare
                         }
            derivedData["players"].append(newPlayer)

            headshotBitmap = urllib.request.urlopen(rawPlayer["headshotImgUrl"])
            output = open("../assets/img/playerBitmaps/" + newPlayer["headshotBitmap"], "wb")
            output.write(headshotBitmap.read())

            if not os.path.isfile("../assets/img/nationBitmaps/" + newPlayer["nation"]["logo"]):
                nationFlagBitmap = urllib.request.urlopen(rawPlayer["nation"]["imageUrls"]["large"])
                output = open("../assets/img/nationBitmaps/" + newPlayer["nation"]["logo"], "wb")
                output.write(nationFlagBitmap.read())

            if not os.path.isfile("../assets/img/clubBadgeBitmaps/" + newPlayer["club"]["logo"]):
                clubBadgeBitmap = urllib.request.urlopen(rawPlayer["club"]["imageUrls"]["dark"]["large"])
                output = open("../assets/img/clubBadgeBitmaps/" + newPlayer["club"]["logo"], "wb")
                output.write(clubBadgeBitmap.read())

            with open(outputFile, 'w+') as f:
                out = json.dumps(derivedData)
                f.write(out)

    if rawData["totalPages"] == pageNumber:  # 666
        break



