/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.17.
 */
const hexValues = [
    '0', '1', '2', '3',
    '4', '5', '6', '7',
    '8', '9', 'a', 'b',
    'c', 'd', 'e', 'f'
];

function getRandomHex() {
    return hexValues[Math.round(Math.random() * 15)];
}

function getRandomHexLetter() {
    return hexValues[9 + Math.ceil(Math.random() * 6)];
}

function getRandomHexes(howMany) {
    let hexes = "";
    for (let i = 0; i < howMany; i++) {
        hexes += getRandomHex();
    }
    return hexes;
}

function getRandomHexesFirstLetter(howMany) {
    let firstLetter = getRandomHexLetter();
    return firstLetter + getRandomHexes(howMany - 1);
}

module.exports = {
    generateUUID: () => {
        const first8 = getRandomHexesFirstLetter(8);
        const then4 = getRandomHexes(4);
        const more4 = getRandomHexes(4);
        const onceMore4 = getRandomHexes(4);
        const last12 = getRandomHexes(12);

        // result is something like b50e8400-e29b-41d4-a716-446655440f00
        return first8 + "-" + then4 + "-" + more4 + "-" + onceMore4 + "-" + last12;
    }
};
