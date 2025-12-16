const BEM = require('imcms-bem-builder');
const $ = require('jquery');

function buildLoadingPage() {
    let $loadingPage = new BEM({
        block: 'imcms-loading-page',
        elements: {
            'load': $('<div>').addClass('loading-animation')
        }
    }).buildBlockStructure('<div>');

    $('body').append($loadingPage);
}

function removeLoadingPage() {
    $('.imcms-loading-page').remove();
}

module.exports = {
    buildLoadingPage: buildLoadingPage,
    removeLoadingPage: removeLoadingPage
};
