# Created by Stephen McVeigh


import urllib.request
import json
import os.path
import get_league_clubs as leagueGetter


def delete_files_in_dir(folder):
    if os.path.exists(folder):
        for the_file in os.listdir(folder):
            file_path = os.path.join(folder, the_file)
            try:
                if os.path.isfile(file_path):
                    os.unlink(file_path)
            except Exception as e:
                print(e)


urlString = "https://www.easports.com/fifa/ultimate-team/api/fut/item?page="

jsonOutputDir = "../app/src/main/assets/player_json"
headshotOutputDir = "../app/src/main/assets/img/playerBitmaps"
nationFlagOutputDir = "../app/src/main/assets/img/nationBitmaps"
clubBadgeOutputDir = "../app/src/main/assets/img/clubBadgeBitmaps"

delete_files_in_dir(headshotOutputDir)
delete_files_in_dir(nationFlagOutputDir)
delete_files_in_dir(clubBadgeOutputDir)
delete_files_in_dir(jsonOutputDir)


pageNumber = 0
derivedData = {"players": []}
retrievedHeadShotUrls = []
leagueJson = leagueGetter.getJsonObject()

print("started scraping player json")
while True:
    pageNumber += 1
    with urllib.request.urlopen(urlString + str(pageNumber)) as url:
        rawData = json.loads(url.read().decode())
    print(str(pageNumber) + "/777")
    for counter in range(0, len(rawData["items"])):
        rawPlayer = rawData["items"][counter]
        if (leagueGetter.club_in_league(rawPlayer["club"]["abbrName"],leagueJson["ENG 1"]) or leagueGetter.club_in_league(rawPlayer["club"]["abbrName"],leagueJson["ENG 2"])) and \
                        rawPlayer["headshotImgUrl"] not in retrievedHeadShotUrls and \
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

            if not os.path.exists(headshotOutputDir):
                os.makedirs(headshotOutputDir)
            if not os.path.exists(nationFlagOutputDir):
                os.makedirs(nationFlagOutputDir)
            if not os.path.exists(clubBadgeOutputDir):
                os.makedirs(clubBadgeOutputDir)
            if not os.path.exists(jsonOutputDir):
                os.makedirs(jsonOutputDir)

            headshotBitmap = urllib.request.urlopen(rawPlayer["headshotImgUrl"])
            retrievedHeadShotUrls.append(rawPlayer["headshotImgUrl"])
            output = open(os.path.join(headshotOutputDir, newPlayer["headshotBitmap"]), "wb")
            output.write(headshotBitmap.read())

            if not os.path.isfile(os.path.join(nationFlagOutputDir, newPlayer["nation"]["logo"])):
                nationFlagBitmap = urllib.request.urlopen(rawPlayer["nation"]["imageUrls"]["large"])
                output = open(os.path.join(nationFlagOutputDir, newPlayer["nation"]["logo"]), "wb")
                output.write(nationFlagBitmap.read())

            if not os.path.isfile(os.path.join(clubBadgeOutputDir, newPlayer["club"]["logo"])):
                clubBadgeBitmap = urllib.request.urlopen(rawPlayer["club"]["imageUrls"]["dark"]["large"])
                output = open(os.path.join(clubBadgeOutputDir, newPlayer["club"]["logo"]), "wb")
                output.write(clubBadgeBitmap.read())

            with open(os.path.join(jsonOutputDir, "all_cards.json"), 'w+') as f:
                out = json.dumps(derivedData)
                f.write(out)

    if rawData["totalPages"] == pageNumber:  # 777
        break



