Imcms.define("imcms-documents-rest-api", ["imcms-rest-api"], function (rest) {

    var api = new rest.API("/documents");


    var currentTime = (function getCurrentTime() {
        var currentDate = new Date(),
            hour = currentDate.getHours(),
            minute = currentDate.getMinutes()
        ;

        if (hour < 10) {
            hour = "0" + hour;
        }
        if (minute < 10) {
            minute = "0" + minute;
        }

        return hour + ":" + minute;
    })();

    var currentDate = (function getCurrentDate() {
        var currentDate = new Date(),
            year = currentDate.getFullYear(),
            month = currentDate.getMonth() + 1,
            date = currentDate.getDate()
        ;

        if (month < 10) {
            month = "0" + month;
        }
        if (date < 10) {
            date = "0" + date;
        }

        return year + "-" + month + "-" + date;
    })();

    //mock data
    var docListMock = [{
        id: 1001,
        alias: "start-page",
        type: "text",
        target: "_blank",
        commonContents: [
            {
                enabled: true,
                title: "Title text",
                menuText: "Menu text",
                language: {
                    id: 1,
                    code: "en",
                    name: "English",
                    nativeName: "English"
                }
            },
            {
                enabled: true,
                title: "Titeltext",
                menuText: "Menytext",
                language: {
                    id: 2,
                    code: "sv",
                    name: "Swedish",
                    nativeName: "Svenska"
                }
            }
        ],
        publicationStatus: 1,
        published: {
            id: 2,
            by: "james_cameron",
            date: currentDate,
            time: currentTime
        },
        archived: {
            by: "t_1000",
            date: currentDate,
            time: currentTime
        },
        publicationEnd: {
            by: "alan_taylor",
            date: currentDate,
            time: currentTime
        },
        modified: {
            by: "john_connor",
            date: currentDate,
            time: currentTime
        },
        created: {
            by: "sarah_connor",
            date: currentDate,
            time: currentTime
        },
        disabledLanguageShowMode: "DO_NOT_SHOW",
        currentVersion: {
            id: 24,
            date: currentDate,
            time: currentTime
        },
        keywords: ["test", "keyword 1", "document"],
        searchDisabled: true,
        categories: [
            {
                id: 1
            },
            {
                id: 4
            },
            {
                id: 5
            },
            {
                id: 7
            }
        ],
        restrictedPermissions: {
            "RESTRICTED_1": {
                edit_text: true,
                edit_menu: false,
                edit_image: true,
                edit_loop: false,
                edit_doc_info: true
            },
            "RESTRICTED_2": {
                edit_text: false,
                edit_menu: true,
                edit_image: false,
                edit_loop: true,
                edit_doc_info: false
            }
        },
        roles: [
            {
                id: 1,
                name: "Useradmin",
                permission: "VIEW"
            },
            {
                id: 2,
                name: "Users",
                permission: "EDIT"
            },
            {
                id: 3,
                name: "Test role",
                permission: "RESTRICTED_1"
            }
        ],
        template: 2,
        childTemplate: 3
    }, {
        id: 1002,
        title: "Second page",
        alias: "second-page",
        type: "text"
    }, {
        id: 1003,
        title: "Another page",
        alias: "some-page",
        type: "text"
    }, {
        id: 1004,
        title: "File page",
        alias: "file-page",
        type: "file"
    }, {
        id: 1005,
        title: "Another page 1",
        alias: "some-page-1",
        type: "text"
    }, {
        id: 1006,
        title: "Another page 2",
        alias: "some-page-2",
        type: "text"
    }, {
        id: 1007,
        title: "Another page 3",
        alias: "some-page-3",
        type: "text"
    }, {
        id: 1008,
        title: "Another page 4",
        alias: "some-page-4",
        type: "text"
    }, {
        id: 1009,
        title: "Another page 5",
        alias: "some-page-5",
        type: "text"
    }, {
        id: 1010,
        title: "Another page 6",
        alias: "some-page-6",
        type: "text"
    }, {
        id: 1011,
        title: "Another page 7",
        alias: "some-page-7",
        type: "text"
    }, {
        id: 1012,
        title: "Another page 8",
        alias: "some-page-8",
        type: "text"
    }, {
        id: 1013,
        title: "Another page 9",
        alias: "some-page-9",
        type: "text"
    }, {
        id: 1014,
        title: "Another page 10",
        alias: "some-page-10",
        type: "text"
    }, {
        id: 1015,
        title: "Another page 11",
        alias: "some-page-11",
        type: "text"
    }, {
        id: 1016,
        title: "Another page 12",
        alias: "some-page-12",
        type: "text"
    }, {
        id: 1017,
        title: "Another page 13",
        alias: "some-page-13",
        type: "text"
    }, {
        id: 1018,
        title: "Another page 14",
        alias: "some-page-14",
        type: "text"
    }];

    api.remove = function (documentId) {
        return {
            done: function (callback) {
                var responses = [200, 500],
                    responseCode = responses[Math.floor(Math.random() * responses.length)];
                if (responseCode === 200) {
                    console.log("%c Document " + documentId + " was removed (not really)", "color: blue;");
                } else {
                    console.log("%c Document " + documentId + " wasn't removed due to some mock circumstances", "color: red;");

                }
                callback(responseCode);
            }
        }
    };

    return api;
});