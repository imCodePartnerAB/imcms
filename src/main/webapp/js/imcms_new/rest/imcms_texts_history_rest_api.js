/**
 * For texts history in Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.01.18
 */
Imcms.define("imcms-texts-history-rest-api", ["imcms-rest-api"], function (rest) {
    // return new rest.API("/texts/history");
    // uncomment after controller implemented

    return {
        read: function (data) {
            return {
                done: function (onDone) {
                    onDone([
                        {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "31-01-2017",
                                time: "23:59"
                            },
                            text: "<p>Text 111 1!!! !21d1d</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "30-01-2017",
                                time: "22:59"
                            },
                            text: "<p>Text 111 1!!! !21</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "29-01-2017",
                                time: "22:05"
                            },
                            text: "<p>Text 111 1!!! !</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "26-01-2017",
                                time: "21:33"
                            },
                            text: "<p>Text 111 1!!!</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "23-01-2017",
                                time: "20:28"
                            },
                            text: "<p>Text 111 1!!</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "21-01-2017",
                                time: "19:28"
                            },
                            text: "<p>Text 111 1!</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "19-01-2017",
                                time: "18:28"
                            },
                            text: "<p>Text 111 1</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "15-01-2017",
                                time: "17:22"
                            },
                            text: "<p>Text 111</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "15-01-2017",
                                time: "16:20"
                            },
                            text: "<p>Text 1</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "07-01-2017",
                                time: "13:09"
                            },
                            text: "<p>Text</p>"
                        }, {
                            modifiedBy: {
                                username: "Admin"
                            },
                            modified: {
                                date: "01-01-2017",
                                time: "00:00"
                            },
                            text: ""
                        }
                    ]);
                }
            };
        }
    };
});
