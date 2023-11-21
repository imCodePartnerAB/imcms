/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.08.18
 */

const fs = require("fs");
const merge = require('webpack-merge');
const path = require('path');
const walkDirectoryRecursively = require("./imcms/js/components/imcms-walk-directory-recursively");

const baseConfig = require('./base.config.js');

const TerserJsPlugin = require("terser-webpack-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const CleanWebpackPlugin = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const WebpackOnBuildPlugin = require("on-build-webpack");

const buildDir = 'dist';

module.exports = merge(baseConfig, {
	mode: 'production',
	optimization: {
		minimizer: [
			new TerserJsPlugin({
				cache: true,
				parallel: true,
				sourceMap: false,
			}),
			new OptimizeCSSAssetsPlugin({
				assetNameRegExp: /\.(s?)css$/,
			}),
		]
	},
	output: {
		path: path.resolve(__dirname, buildDir)
	},
	plugins: [
		new CleanWebpackPlugin([buildDir]),
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
});
