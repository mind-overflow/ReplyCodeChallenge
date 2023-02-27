// import libraries
const fs = require("fs");

// class used to identify single building objects
class Building {
    constructor(x, y, latency_weight, speed_weight) {
        this.x = x;
        this.y = y;
        this.latency_weight = latency_weight; // how important the latency is
        this.speed_weight = speed_weight; // how important the speed is
    }
}

// class used to identify placeable antennas
class Antenna {
    constructor(x, y, range, conn_speed) {
        this.x = x;
        this.y = y;
        this.range = range; // how many "squares" this antenna covers
        this.conn_speed = conn_speed; // connection speed
    }
}

// compute distance between a building and an antenna (manhattan distance)
function computeDistance(building, antenna) {
    return (building.x - antenna.x) + (building.y - antenna.y);
}

// returns a sorted (based on highest score) array of all antennas reachable from a specific building
function getReachableAntennas(building) {
    let reachableAntennas = [];

    for (let i = 0; i < antennas.length; i++) {

        let antenna = antennas[i];
        if(antenna.range >= computeDistance(building, antenna)) {
            reachableAntennas.push(antenna);
        }
    }

    reachableAntennas.sort((a, b) => computeConnectionScore(building, a) > computeConnectionScore((building, b)) ? 1 : -1);

    return reachableAntennas;
}


function computeConnectionScore(building, antenna) {
    return building.speed_weight * antenna.conn_speed - building.latency_weight * computeDistance(building, antenna);
}

// check which antenna a specific building will connect to; NULL if no nearby antenna is found
function getBestAntenna(building) {
    let antennas = getReachableAntennas(building);
    if (antennas.length > 0) {
        return antennas[0];
    } else {
        return null;
    }
}

// compute specific score of a building based on the connection score; 0 if no antenna is found
function computeBuildingScore(building) {

    let antenna = getBestAntenna(building);

    if(antenna == null) return 0;

    return computeConnectionScore(building, antenna);
}

// returns an array of all the buildings that currently have no nearby antenna to connect to
function missingBuildingAntennas() {

    let missingBuildings = [];

    for (let i = 0; i < buildings.length; i++) {
        let building = buildings[i];

        if(computeBuildingScore(building) <= 0) {
            missingBuildings.push(building);
        }
    }

    return missingBuildings;
}

function computeReward() {
    if(missingBuildingAntennas().length > 0) return 0;
    return reward;
}

function computeTotalScore() {
    let score = 0;

    for (let i = 0; i < buildings.length; i++) {
        let building = buildings[i];
        score += computeBuildingScore(building);
    }

    return score + reward;
}

// get the medium point between two buildings
function getMidPoint(buildingA, buildingB) {

    return {x: parseInt(((buildingA.x + buildingB.x)/2).toFixed()), y: parseInt(((buildingA.y + buildingB.y)/2).toFixed())};
}

// returns an array of all the points where we should consider placing antennas,
// to avoid brute-forcing the whole grid and instead focus on a few specific locations.
function getLikelyAntennaPoints() {

    let points = [];

    // compute all medium points between buildings
    for(let i = 0; i < buildings.length; i++) {
        let buildingA = buildings[i];

        for(let j = i+1; j < buildings.length; j++) {

            let buildingB = buildings[j];

            let point = getMidPoint(buildingA, buildingB);
            let skip = false;

            for(let j = 0; j < points.length; j++) {
                let currPoint = points[j];
                if(currPoint.x === point.x && currPoint.y === point.y)
                {
                    skip = true;
                    break;
                }
            }

            if(!skip)
                points.push(point);
        }
    }

    // add all buildings to the list, since buildings can hold antennas themselves
    // and are thus valid candidates
    for(let i = 0; i < buildings.length; i++) {
        let building = buildings[i];

        let skip = false;

        for(let j = 0; j < points.length; j++) {
            let currPoint = points[j];
            if(currPoint.x === building.x && currPoint.y === building.y)
            {
                skip = true;
                break;
            }
        }

        if(!skip)
        {
            points.push({
                x: parseInt(building.x),
                y: parseInt(building.y)
            })
        }
    }

    return points;
}

function getBestPointForAntenna(antenna)
{
    // todo
}

// <debug> save starting time in milliseconds since epoch
let startTime = Date.now();

// init needed variables
let height, width;
let buildingsAmount, availableAntennas, reward;
let buildings = [];
let antennas = [];

// open input file
fs.readFile("data_scenarios_a_example.in", (err, f) => {

    // parse input file
    let fileString = f.toString();
    let fileLines = fileString.split("\n");

    let dimensions = fileLines[0].split(" ");

    height = parseInt(dimensions[0]);
    width = parseInt(dimensions[1]);

    let buildingsInfo = fileLines[1].split(" ");

    buildingsAmount = parseInt(buildingsInfo[0]);
    availableAntennas = parseInt(buildingsInfo[1]);
    reward = parseInt(buildingsInfo[2]);

    for(let index = 2; index < buildingsAmount+2; index++)
    // ^ building info starts at line 2
    {
        let buildingInfo = fileLines[index].split(" ")
        let building = new Building(parseInt(buildingInfo[0]), parseInt(buildingInfo[1]), parseInt(buildingInfo[2]), parseInt(buildingInfo[3]))

        buildings.push(building)
    }

    for(let index = 2 + buildingsAmount; index < 2 + buildingsAmount + availableAntennas - 1; index++)
    // ^ antennas info starts after buildings
    {
        let antennaInfo = fileLines[index].split(" ");
        let antenna = new Antenna(null, null, parseInt(antennaInfo[0]), parseInt(antennaInfo[1]));

        antennas.push(antenna)
    }

    // sort antennas by range (highest to lowest)
    antennas.sort((a, b) => parseInt(a.range) < parseInt(b.range) ? 1 : -1);

    // compute all points where we should consider placing antennas
    let likelyAntennaPoints = getLikelyAntennaPoints();

    // print map legend
    console.log("-------------------------------")
    console.log("| City Map:                   |")
    console.log("| □ = empty                   |")
    console.log("| ■ = building                |")
    console.log("| ▲ = antenna                 |")
    console.log("| ▣ = building with antenna   |")
    console.log("| △ = possibly good position  |")
    console.log("-------------------------------\n")

    // print city map
    for(let y = 0; y  < width; y++) {
        for(let x = 0; x < height; x++) {
            let found = 0;

            for (let i = 0; i < buildings.length; i++) {
                let building = buildings[i];

                if(building.x === x && building.y === y) {
                    process.stdout.write("■")
                    found = 1;
                    break;
                }
            }

            for(let i = 0; i < antennas.length; i++) {
                let antenna = antennas[i];

                if(antenna.x === x && antenna.y === y) {
                    if(found === 1) found = 3;
                    else
                    {
                        found = 2;
                        process.stdout.write("▲")
                    }
                    break;
                }
            }

            for(let i = 0; i < likelyAntennaPoints.length; i++) {
                let point = likelyAntennaPoints[i];

                if(point.x === x && point.y === y)
                {
                    if(found === 0)
                    {
                        process.stdout.write("△");
                        found = 4;
                    }
                }

            }

            if(found === 0)
                process.stdout.write("□");
            else if(found === 3)
                process.stdout.write("▣");
        }
        process.stdout.write("\n");
    }

    // <debug> log places where we should consider placing antennas
    console.log("\nLikely antenna points: ");
    console.log(getLikelyAntennaPoints());

    // <debug> save ending time in milliseconds since epoch
    let endTime = Date.now();

    // <debug> log total runtime
    console.log("\nRunning took " + (endTime-startTime) + "ms");
})

