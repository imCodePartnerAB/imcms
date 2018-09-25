/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.09.18
 */
const CleanWebpackPlugin = require('clean-webpack-plugin');
const path = require('path');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const clientConfig = require('./client.config.js');

const buildDir = path.resolve(__dirname, clientConfig.destination);

const fs = require('fs');
const WebpackOnBuildPlugin = require('on-build-webpack');

module.exports = {
    mode: 'production',
    entry: clientConfig.entry,
    optimization: {
        minimizer: [
            new OptimizeCSSAssetsPlugin({
                assetNameRegExp: /\.(s?)css$/,
            })
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
        new CleanWebpackPlugin([buildDir]),
        new MiniCssExtractPlugin('[name].css'),
        new WebpackOnBuildPlugin(() => {
            fs.readdir(path.resolve(buildDir), (err, files) => {

                if (err) {
                    console.log(err);
                    return;
                }

                const jsPattern = /.js$/;

                files.forEach(file => {
                    if (jsPattern.test(file)) {
                        fs.unlink(path.resolve(buildDir, file));
                    }
                });
            });
        }),
    ],
};
