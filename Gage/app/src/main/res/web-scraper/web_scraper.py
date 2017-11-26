import urllib.request as getImage
import json
import scipy.stats as stats

def generateAttributes(rating):
    lowestBound, highestBound = 20, 99
    mean = rating
    stdDeviation = 100 - rating
    if (stdDeviation > 20):
        stdDeviation = 20
    dist = stats.truncnorm((lowestBound - mean) / stdDeviation, (highestBound - mean) / stdDeviation, loc=mean, scale=stdDeviation)
    values = dist.rvs(6)
    for x in range(0, len(values)):
        values[x] = int(values[x])
    return values

rawData = json.load(open('raw_players.json'))
outputFile = "../playerValues/derived_players.json"

derivedData = {"Players":[]}

for counter in range(0, len(rawData["Players"])) :
    try:
        print(str(counter) + "/" + str(len(rawData["Players"])))
        rawPlayer = rawData["Players"][counter]
        resource = getImage.urlopen("https://fifa17.content.easports.com/fifa/fltOnlineAssets/CC8267B6-0817-4842-BB6A-A20F88B05418/2017/fut/items"
                                    "/images/players/html5/120x120/" + str(rawPlayer["id"]) + ".png")
        newPlayerAttributes = generateAttributes(rawPlayer["r"])
        newID = str(len(derivedData["Players"]))
        output = open("../../assets/img/playerBitmaps/" + newID + ".png", "wb")
        output.write(resource.read())
        if ("c" in rawPlayer):
            newPlayer = {"id":newID, "firstname":rawPlayer["f"],"surname": rawPlayer["l"],"nickname":rawPlayer["c"],
                     "rating": rawPlayer["r"],"shooting":newPlayerAttributes[0],"passing":newPlayerAttributes[1],
                     "dribbling":newPlayerAttributes[2],"heading":newPlayerAttributes[3],"pace":newPlayerAttributes[4],
                     "defending":newPlayerAttributes[5]}
        else:
            newPlayer = {"id": newID, "firstname": rawPlayer["f"], "surname": rawPlayer["l"],
                         "rating": rawPlayer["r"], "shooting": newPlayerAttributes[0], "passing": newPlayerAttributes[1],
                         "dribbling": newPlayerAttributes[2], "heading": newPlayerAttributes[3],
                         "pace": newPlayerAttributes[4],
                         "defending": newPlayerAttributes[5]}

        derivedData["Players"].append(newPlayer)

        with open(outputFile, 'w+') as f:
            out = json.dumps(derivedData)
            f.write(out)
    except:
        print("Error 404")
