const path = require("path");
const fs = require("fs");
const pathResolver = require("./dependency_path_resolver");
const walkDirectoryRecursively = require("./imcms/js/components/imcms-walk-directory-recursively");

const scssBaseConfig = require("./scss.base.config");
const baseConfig = require("./base.config");
const clientConfig = require("./client.config");

const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const WebpackOnBuildPlugin = require("on-build-webpack");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");

const buildDir = path.resolve(__dirname, scssBaseConfig.destination);

module.exports = {
	mode: 'production',
	entry: getEntries(),
	optimization: {
		minimizer: [
			new OptimizeCSSAssetsPlugin({
				assetNameRegExp: /\.(s?)css$/,
			}),
		]
	},
	output: {
		path: buildDir,
		filename: '[name].js',
	},
	module: {
		rules: [
			{
				test: /\.(s?)css$/,
				use: [MiniCssExtractPlugin.loader, 'css-loader', 'sass-loader']
			}
		]
	},
	plugins: [
		new MiniCssExtractPlugin('[name].css'),
		new WebpackOnBuildPlugin(async () => {
			const jsPattern = /.js$/;
			const files = await walkDirectoryRecursively(path.resolve(buildDir));

			files.forEach(file => {
				const fileName = file.split('/').pop();
				const fileNameWithoutExtension = fileName.split('.').slice(0, -1).join('.');

				if (jsPattern.test(file) && baseConfig.entry[fileNameWithoutExtension] === undefined) {
					fs.unlink(file, (err) => {
						if (err) console.log(err);
						else console.log(`Deleted unnecessary file: ${file}`)
					});
				}
			});
		}),
	],
};

function getEntries() {
	if (!clientConfig.rebuildImcmsCSS) return clientConfig.entry;

	return {
		...clientConfig.entry,
		...pathResolver.resolveCSSPaths("./imcms/css", scssBaseConfig.entry)
	};
}
