/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 17.08.17.
 */
Imcms.define("imcms-files-rest-api", ["imcms-rest-api"], function (rest) {

    var api = new rest.API("/api/files");

    var mockResponse = [{
        name: "images",
        path: "images",
        files: [
            {
                name: "sample1",
                path: "img/choose_img/img1.png",
                format: "jpg",
                uploaded: "15.02.2017",
                resolution: "635x120",
                size: "37kB"
            }, {
                name: "sample3",
                path: "img/choose_img/img3.png",
                format: "bmp",
                uploaded: "17.02.2017",
                resolution: "635x120",
                size: "34kB"
            }, {
                name: "sample4",
                path: "img/choose_img/img4.png",
                format: "gif",
                uploaded: "18.02.2017",
                resolution: "64x64",
                size: "16kB"
            }
        ],
        folders: [
            {
                name: "images_2019",
                path: "images/images_2019",
                files: [
                    {
                        name: "sample1",
                        path: "img/choose_img/img1.png",
                        format: "jpg",
                        uploaded: "15.02.2017",
                        resolution: "635x120",
                        size: "37kB"
                    }, {
                        name: "sample2",
                        path: "img/choose_img/img2.png",
                        format: "png",
                        uploaded: "16.02.2017",
                        resolution: "635x120",
                        size: "35kB"
                    }, {
                        name: "sample4",
                        path: "img/choose_img/img4.png",
                        format: "gif",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB"
                    }
                ],
                folders: []
            }, {
                name: "images_2018",
                path: "images/images_2018",
                files: [
                    {
                        name: "sample3",
                        path: "img/choose_img/img3.png",
                        format: "bmp",
                        uploaded: "17.02.2017",
                        resolution: "635x120",
                        size: "34kB"
                    }, {
                        name: "sample4",
                        path: "img/choose_img/img4.png",
                        format: "gif",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB"
                    }
                ],
                folders: [
                    {
                        name: "flowers",
                        path: "images/images_2018/flowers",
                        files: [
                            {
                                name: "sample1",
                                path: "img/choose_img/img1.png",
                                format: "jpg",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB"
                            }, {
                                name: "sample2",
                                path: "img/choose_img/img2.png",
                                format: "png",
                                uploaded: "16.02.2017",
                                resolution: "635x120",
                                size: "35kB"
                            }, {
                                name: "sample3",
                                path: "img/choose_img/img3.png",
                                format: "bmp",
                                uploaded: "17.02.2017",
                                resolution: "635x120",
                                size: "34kB"
                            }
                        ],
                        folders: [
                            {
                                name: "black",
                                path: "images/images_2018/flowers/black",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "img/choose_img/img1.png",
                                        format: "jpg",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB"
                                    }, {
                                        name: "sample2",
                                        path: "img/choose_img/img2.png",
                                        format: "png",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB"
                                    }, {
                                        name: "sample4",
                                        path: "img/choose_img/img4.png",
                                        format: "gif",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB"
                                    }
                                ],
                                folders: []
                            }, {
                                name: "rose",
                                path: "images/images_2018/flowers/rose",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "img/choose_img/img1.png",
                                        format: "jpg",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB"
                                    }, {
                                        name: "sample2",
                                        path: "img/choose_img/img2.png",
                                        format: "png",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB"
                                    }, {
                                        name: "sample3",
                                        path: "img/choose_img/img3.png",
                                        format: "bmp",
                                        uploaded: "17.02.2017",
                                        resolution: "635x120",
                                        size: "34kB"
                                    }, {
                                        name: "sample4",
                                        path: "img/choose_img/img4.png",
                                        format: "gif",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB"
                                    }
                                ],
                                folders: [{
                                    name: "beer",
                                    path: "images/images_2018/flowers/rose/beer",
                                    files: [
                                        {
                                            name: "sample1",
                                            path: "img/choose_img/img1.png",
                                            format: "jpg",
                                            uploaded: "15.02.2017",
                                            resolution: "635x120",
                                            size: "37kB"
                                        }, {
                                            name: "sample2",
                                            path: "img/choose_img/img2.png",
                                            format: "png",
                                            uploaded: "16.02.2017",
                                            resolution: "635x120",
                                            size: "35kB"
                                        }, {
                                            name: "sample3",
                                            path: "img/choose_img/img3.png",
                                            format: "bmp",
                                            uploaded: "17.02.2017",
                                            resolution: "635x120",
                                            size: "34kB"
                                        }
                                    ],
                                    folders: []
                                }]
                            }
                        ]
                    }
                ]
            }, {
                name: "images_2017",
                path: "images/images_2017",
                files: [
                    {
                        name: "sample2",
                        path: "img/choose_img/img2.png",
                        format: "png",
                        uploaded: "16.02.2017",
                        resolution: "635x120",
                        size: "35kB"
                    }, {
                        name: "sample3",
                        path: "img/choose_img/img3.png",
                        format: "bmp",
                        uploaded: "17.02.2017",
                        resolution: "635x120",
                        size: "34kB"
                    }, {
                        name: "sample4",
                        path: "img/choose_img/img4.png",
                        format: "gif",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB"
                    }
                ],
                folders: [
                    {
                        name: "cars",
                        path: "images/images_2017/cars",
                        files: [
                            {
                                name: "sample1",
                                path: "img/choose_img/img1.png",
                                format: "jpg",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB"
                            }, {
                                name: "sample4",
                                path: "img/choose_img/img4.png",
                                format: "gif",
                                uploaded: "18.02.2017",
                                resolution: "64x64",
                                size: "16kB"
                            }
                        ],
                        folders: [
                            {
                                name: "bmw",
                                path: "images/images_2017/cars/bmw",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "img/choose_img/img1.png",
                                        format: "jpg",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB"
                                    }
                                ],
                                folders: []
                            }, {
                                name: "lada",
                                path: "images/images_2017/cars/lada",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "img/choose_img/img1.png",
                                        format: "jpg",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB"
                                    }, {
                                        name: "sample2",
                                        path: "img/choose_img/img2.png",
                                        format: "png",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB"
                                    }, {
                                        name: "sample3",
                                        path: "img/choose_img/img3.png",
                                        format: "bmp",
                                        uploaded: "17.02.2017",
                                        resolution: "635x120",
                                        size: "34kB"
                                    }, {
                                        name: "sample4",
                                        path: "img/choose_img/img4.png",
                                        format: "gif",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB"
                                    }
                                ],
                                folders: [{
                                    name: "kalyna",
                                    path: "images/images_2017/cars/lada/kalyna",
                                    files: [
                                        {
                                            name: "sample2",
                                            path: "img/choose_img/img2.png",
                                            format: "png",
                                            uploaded: "16.02.2017",
                                            resolution: "635x120",
                                            size: "35kB"
                                        }, {
                                            name: "sample3",
                                            path: "img/choose_img/img3.png",
                                            format: "bmp",
                                            uploaded: "17.02.2017",
                                            resolution: "635x120",
                                            size: "34kB"
                                        }
                                    ],
                                    folders: []
                                }]
                            }
                        ]
                    }, {
                        name: "holiday",
                        path: "images/images_2017/holiday",
                        files: [
                            {
                                name: "sample1",
                                path: "img/choose_img/img1.png",
                                format: "jpg",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB"
                            }, {
                                name: "sample3",
                                path: "img/choose_img/img3.png",
                                format: "bmp",
                                uploaded: "17.02.2017",
                                resolution: "635x120",
                                size: "34kB"
                            }, {
                                name: "sample4",
                                path: "img/choose_img/img4.png",
                                format: "gif",
                                uploaded: "18.02.2017",
                                resolution: "64x64",
                                size: "16kB"
                            }
                        ],
                        folders: []
                    }
                ]
            }, {
                name: "images_2016",
                path: "images/images_2016",
                files: [
                    {
                        name: "sample1",
                        path: "img/choose_img/img1.png",
                        format: "jpg",
                        uploaded: "15.02.2017",
                        resolution: "635x120",
                        size: "37kB"
                    }, {
                        name: "sample4",
                        path: "img/choose_img/img4.png",
                        format: "gif",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB"
                    }
                ],
                folders: [
                    {
                        name: "summer",
                        path: "images/images_2016/summer",
                        files: [
                            {
                                name: "sample1",
                                path: "img/choose_img/img1.png",
                                format: "jpg",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB"
                            }, {
                                name: "sample2",
                                path: "img/choose_img/img2.png",
                                format: "png",
                                uploaded: "16.02.2017",
                                resolution: "635x120",
                                size: "35kB"
                            }
                        ],
                        folders: [
                            {
                                name: "img",
                                path: "images/images_2016/summer/img",
                                files: [
                                    {
                                        name: "sample2",
                                        path: "img/choose_img/img2.png",
                                        format: "png",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB"
                                    }, {
                                        name: "sample3",
                                        path: "img/choose_img/img3.png",
                                        format: "bmp",
                                        uploaded: "17.02.2017",
                                        resolution: "635x120",
                                        size: "34kB"
                                    }, {
                                        name: "sample4",
                                        path: "img/choose_img/img4.png",
                                        format: "gif",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB"
                                    }
                                ],
                                folders: []
                            }, {
                                name: "family",
                                path: "images/images_2016/summer/family",
                                files: [
                                    {
                                        name: "sample2",
                                        path: "img/choose_img/img2.png",
                                        format: "png",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB"
                                    }, {
                                        name: "sample4",
                                        path: "img/choose_img/img4.png",
                                        format: "gif",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB"
                                    }
                                ],
                                folders: [{
                                    name: "photo",
                                    path: "images/images_2016/summer/family/photo",
                                    files: [
                                        {
                                            name: "sample1",
                                            path: "img/choose_img/img1.png",
                                            format: "jpg",
                                            uploaded: "15.02.2017",
                                            resolution: "635x120",
                                            size: "37kB"
                                        }, {
                                            name: "sample2",
                                            path: "img/choose_img/img2.png",
                                            format: "png",
                                            uploaded: "16.02.2017",
                                            resolution: "635x120",
                                            size: "35kB"
                                        }, {
                                            name: "sample3",
                                            path: "img/choose_img/img3.png",
                                            format: "bmp",
                                            uploaded: "17.02.2017",
                                            resolution: "635x120",
                                            size: "34kB"
                                        }, {
                                            name: "sample4",
                                            path: "img/choose_img/img4.png",
                                            format: "gif",
                                            uploaded: "18.02.2017",
                                            resolution: "64x64",
                                            size: "16kB"
                                        }
                                    ],
                                    folders: []
                                }]
                            }
                        ]
                    }, {
                        name: "holiday",
                        path: "images/images_2016/holiday",
                        files: [
                            {
                                name: "sample1",
                                path: "img/choose_img/img1.png",
                                format: "jpg",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB"
                            }
                        ],
                        folders: []
                    }, {
                        name: "spring",
                        path: "images/images_2016/spring",
                        files: [
                            {
                                name: "sample1",
                                path: "img/choose_img/img1.png",
                                format: "jpg",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB"
                            }, {
                                name: "sample3",
                                path: "img/choose_img/img3.png",
                                format: "bmp",
                                uploaded: "17.02.2017",
                                resolution: "635x120",
                                size: "34kB"
                            }, {
                                name: "sample4",
                                path: "img/choose_img/img4.png",
                                format: "gif",
                                uploaded: "18.02.2017",
                                resolution: "64x64",
                                size: "16kB"
                            }
                        ],
                        folders: []
                    }
                ]
            }
        ]
    }];

    //mock data
    api.create = function (data) {
        return {
            done: function (callback) {
                console.log("%c Creating new file: ", "color: blue;");
                console.log(data);
                callback({ // mock response object - empty folder
                    name: data.name,
                    path: data.path,
                    files: [],
                    folders: []
                });
            }
        }
    };

    api.read = function (path) {
        return {
            done: function (callback) {
                console.log("%c Reading files: ", "color: blue;");
                console.log(path);
                callback(mockResponse);
            }
        }
    };

    api.update = function (data) {
        return {
            done: function (callback) {
                console.log("%c Updating file: ", "color: blue;");
                console.log(data);
                callback({
                    status: "OK",
                    code: 200
                });
            }
        }
    };

    api.remove = function (path) {
        return {
            done: function (callback) {
                console.log("%c " + path + " was removed (not really)", "color: blue;");
                callback.call();
            }
        }
    };

    return api;
});
