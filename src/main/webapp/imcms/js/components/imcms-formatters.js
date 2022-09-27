module.exports = {
	//e.g. [word1, word2, word3, ...] => word1, word2 and word3
	arrayOfStringsToFormattedString(arr) {
		let outStr = "";
		if (arr.length === 1) {
			outStr = arr[0];
		} else if (arr.length === 2) {
			outStr = arr.join(' and ');
		} else if (arr.length > 2) {
			outStr = arr.slice(0, -1).join(', ') + ', and ' + arr.slice(-1);
		}
		return outStr;
	}
}
