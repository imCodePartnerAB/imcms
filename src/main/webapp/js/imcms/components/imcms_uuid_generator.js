/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.17.
 */
define("imcms-uuid-generator", [], function () {
    var hexValues = [
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f'
    ];

    function getRandomHex() {
        return hexValues[Math.round(Math.random() * 15)];
    }

    function getRandomHexes(howMany) {
        var hexes = "";
        for (var i = 0; i < howMany; i++) {
            hexes += getRandomHex();
        }
        return hexes;
    }

    return {
        generateUUID: function () {
            var first8 = getRandomHexes(8),
                then4 = getRandomHexes(4),
                more4 = getRandomHexes(4),
                onceMore4 = getRandomHexes(4),
                last12 = getRandomHexes(12)
            ;
            // result is something like 550e8400-e29b-41d4-a716-446655440f00
            return first8 + "-" + then4 + "-" + more4 + "-" + onceMore4 + "-" + last12;
        }
    }
});
