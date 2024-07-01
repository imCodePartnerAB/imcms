const merge = require('webpack-merge');
const baseConfig = require('./base.config.js');
const path = require('path');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const WebpackOnBuildPlugin = require("on-build-webpack");
const walkDirectoryRecursively = require("./imcms/js/components/imcms-walk-directory-recursively");
const fs = require("fs");
const buildDir = './../../../build/libs/exploded/imcms-6.0.0-beta23-SNAPSHOT.war/dist';

module.exports = merge(baseConfig, {
    mode: 'development',
    watch: true,
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
