/**
 * Client configuration used by SCSS compiler. You have to declare relative
 * compiled files destination path. And also you have to declare input entries,
 * where key is output file name and value is relative path to the source file.
 *
 * @example
 *  module.exports = {
 *      entry: {
 *          main: './css/test.scss'
 *      },
 *      destination: 'dist-css'
 *  };
 *
 *  // Result files structure would be: './dist-css/main.css'
 */
module.exports = {
    entry: {
        main: './css/test.scss'
    },
	rebuildImcmsCSS: false,
    destination: 'dist-css'
};
