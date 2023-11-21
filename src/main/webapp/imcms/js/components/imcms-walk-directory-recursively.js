const fs = require('fs/promises');
const path = require('path');

/**
 * async walks directory recursively and returns list of absolute paths
 * @param directory
 * @param fileList optional
 * @returns {Promise<[string]>}
 */
async function walkDirectoryRecursively(directory, fileList = []) {
	const files = await fs.readdir(directory);

	for (let file of files) {
		const filePath = path.join(directory, file);
		const stat = await fs.stat(filePath);

		if (stat.isDirectory()) {
			fileList = await walkDirectoryRecursively(filePath, fileList);
		} else {
			fileList.push(`${directory}/${file}`);
		}
	}

	return fileList;
}

module.exports = walkDirectoryRecursively;
