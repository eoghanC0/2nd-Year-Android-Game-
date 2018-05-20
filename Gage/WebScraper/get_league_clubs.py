# Created by Stephen McVeigh


import urllib.request
import json
import os


def delete_loaning_clubs():
    for league in derivedData:
        tempClubs = []
        deleteIndexes = []
        for i in range(0,len(derivedData[league])):
            if derivedData[league][i]["numOfPlayers"] < 5:
                deleteIndexes.append(i)
        for i in range(0,len(derivedData[league])):
            if i not in deleteIndexes:
                tempClubs.append(derivedData[league][i])
        derivedData[league] = tempClubs


def get_index_of_club_in_league(clubName, league):
    counter = 0
    for club in league:
        if club["name"] == clubName:
            return counter
        counter += 1
    return -1


def club_in_league(clubName, league):
    for club in league:
        if club["name"] == clubName:
            return True
    return False


def get_clubs():
    print("Started getting clubs/leagues")
    pageNumber = 0
    while True:
        pageNumber += 1
        print(str(pageNumber) + "/777")
        with urllib.request.urlopen(urlString + str(pageNumber)) as url:
            rawData = json.loads(url.read().decode())
        for rawPlayer in rawData["items"]:
            if rawPlayer["league"]["abbrName"] not in derivedData:
                newClub = {"name": rawPlayer["club"]["abbrName"], "numOfPlayers": 1}
                derivedData[rawPlayer["league"]["abbrName"]] = []
                derivedData[rawPlayer["league"]["abbrName"]].append(newClub)
            else:
                if not club_in_league(rawPlayer["club"]["abbrName"], derivedData[rawPlayer["league"]["abbrName"]]):
                    newClub = {"name": rawPlayer["club"]["abbrName"], "numOfPlayers": 1}
                    derivedData[rawPlayer["league"]["abbrName"]].append(newClub)
                else:
                    derivedData[rawPlayer["league"]["abbrName"]][get_index_of_club_in_league(rawPlayer["club"]["abbrName"], derivedData[rawPlayer["league"]["abbrName"]])]["numOfPlayers"] += 1

        if rawData["totalPages"] == pageNumber:  # 777
            break

    delete_loaning_clubs()

    with open(jsonOutputFile, 'w') as f:
        out = json.dumps(derivedData)
        f.write(out)


def getJsonObject():
    return json.load(open(jsonOutputFile))


urlString = "https://www.easports.com/fifa/ultimate-team/api/fut/item?page="

jsonOutputFile = "leagueClubs.json"
derivedData = {}

if not os.path.isfile(jsonOutputFile):
    get_clubs()





