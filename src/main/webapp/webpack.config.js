const merge = require('webpack-merge');
const baseConfig = require('./base.config.js');
const path = require('path');
const CleanWebpackPlugin = require('clean-webpack-plugin');

module.exports = merge(baseConfig, {
    mode: 'development',
    watch: true,
    output: {
        path: path.resolve(__dirname, './../../../build/libs/exploded/imcms-6.0.0-beta19-SNAPSHOT.war/dist')
    },
    plugins: [
        new CleanWebpackPlugin(['./../../../build/libs/exploded/imcms-6.0.0-beta19-SNAPSHOT.war/dist'])
    ],
});
