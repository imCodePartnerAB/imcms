/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 17.08.17.
 */
Imcms.define("imcms-image-files-rest-api", ["imcms-rest-api"], function (rest) {

    var api = new rest.API("/files");

    var mockResponse = [{
        name: "images",
        path: "images",
        files: [
            {
                name: "sample1",
                path: "/images/img1.jpg",
                format: "JPEG",
                uploaded: "15.02.2017",
                resolution: "635x120",
                size: "37kB",
                width: 5509,
                height: 3673,
                cropRegion: {
                    cropX1: -1,
                    cropY1: -1,
                    cropX2: -1,
                    cropY2: -1
                }
            }, {
                name: "sample3",
                path: "/images/img3.png",
                format: "BMP",
                uploaded: "17.02.2017",
                resolution: "635x120",
                size: "34kB",
                width: 32,
                height: 29
            }, {
                name: "sample4",
                path: "/images/img4.png",
                format: "GIF",
                uploaded: "18.02.2017",
                resolution: "64x64",
                size: "16kB",
                width: 102,
                height: 146
            }
        ],
        folders: [
            {
                name: "images_2019",
                path: "images/images_2019",
                files: [
                    {
                        name: "sample1",
                        path: "/images/img1.jpg",
                        format: "JPEG",
                        uploaded: "15.02.2017",
                        resolution: "635x120",
                        size: "37kB",
                        width: 5509,
                        height: 3673
                    }, {
                        name: "sample2",
                        path: "/images/img2.png",
                        format: "PNG",
                        uploaded: "16.02.2017",
                        resolution: "635x120",
                        size: "35kB",
                        width: 147,
                        height: 146
                    }, {
                        name: "sample4",
                        path: "/images/img4.png",
                        format: "GIF",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB",
                        width: 102,
                        height: 146
                    }
                ],
                folders: []
            }, {
                name: "images_2018",
                path: "images/images_2018",
                files: [
                    {
                        name: "sample3",
                        path: "/images/img3.png",
                        format: "BMP",
                        uploaded: "17.02.2017",
                        resolution: "635x120",
                        size: "34kB",
                        width: 32,
                        height: 29
                    }, {
                        name: "sample4",
                        path: "/images/img4.png",
                        format: "GIF",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB",
                        width: 102,
                        height: 146
                    }
                ],
                folders: [
                    {
                        name: "flowers",
                        path: "images/images_2018/flowers",
                        files: [
                            {
                                name: "sample1",
                                path: "/images/img1.jpg",
                                format: "JPEG",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB",
                                width: 5509,
                                height: 3673
                            }, {
                                name: "sample2",
                                path: "/images/img2.png",
                                format: "PNG",
                                uploaded: "16.02.2017",
                                resolution: "635x120",
                                size: "35kB",
                                width: 147,
                                height: 146
                            }, {
                                name: "sample3",
                                path: "/images/img3.png",
                                format: "BMP",
                                uploaded: "17.02.2017",
                                resolution: "635x120",
                                size: "34kB",
                                width: 32,
                                height: 29
                            }
                        ],
                        folders: [
                            {
                                name: "black",
                                path: "images/images_2018/flowers/black",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "/images/img1.jpg",
                                        format: "JPEG",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB",
                                        width: 5509,
                                        height: 3673
                                    }, {
                                        name: "sample2",
                                        path: "/images/img2.png",
                                        format: "PNG",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB",
                                        width: 147,
                                        height: 146
                                    }, {
                                        name: "sample4",
                                        path: "/images/img4.png",
                                        format: "GIF",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB",
                                        width: 102,
                                        height: 146
                                    }
                                ],
                                folders: []
                            }, {
                                name: "rose",
                                path: "images/images_2018/flowers/rose",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "/images/img1.jpg",
                                        format: "JPEG",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB",
                                        width: 5509,
                                        height: 3673
                                    }, {
                                        name: "sample2",
                                        path: "/images/img2.png",
                                        format: "PNG",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB",
                                        width: 147,
                                        height: 146
                                    }, {
                                        name: "sample3",
                                        path: "/images/img3.png",
                                        format: "BMP",
                                        uploaded: "17.02.2017",
                                        resolution: "635x120",
                                        size: "34kB",
                                        width: 32,
                                        height: 29
                                    }, {
                                        name: "sample4",
                                        path: "/images/img4.png",
                                        format: "GIF",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB",
                                        width: 102,
                                        height: 146
                                    }
                                ],
                                folders: [{
                                    name: "beer",
                                    path: "images/images_2018/flowers/rose/beer",
                                    files: [
                                        {
                                            name: "sample1",
                                            path: "/images/img1.jpg",
                                            format: "JPEG",
                                            uploaded: "15.02.2017",
                                            resolution: "635x120",
                                            size: "37kB",
                                            width: 5509,
                                            height: 3673
                                        }, {
                                            name: "sample2",
                                            path: "/images/img2.png",
                                            format: "PNG",
                                            uploaded: "16.02.2017",
                                            resolution: "635x120",
                                            size: "35kB",
                                            width: 147,
                                            height: 146
                                        }, {
                                            name: "sample3",
                                            path: "/images/img3.png",
                                            format: "BMP",
                                            uploaded: "17.02.2017",
                                            resolution: "635x120",
                                            size: "34kB",
                                            width: 32,
                                            height: 29
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
                        path: "/images/img2.png",
                        format: "PNG",
                        uploaded: "16.02.2017",
                        resolution: "635x120",
                        size: "35kB",
                        width: 147,
                        height: 146
                    }, {
                        name: "sample3",
                        path: "/images/img3.png",
                        format: "BMP",
                        uploaded: "17.02.2017",
                        resolution: "635x120",
                        size: "34kB",
                        width: 32,
                        height: 29
                    }, {
                        name: "sample4",
                        path: "/images/img4.png",
                        format: "GIF",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB",
                        width: 102,
                        height: 146
                    }
                ],
                folders: [
                    {
                        name: "cars",
                        path: "images/images_2017/cars",
                        files: [
                            {
                                name: "sample1",
                                path: "/images/img1.jpg",
                                format: "JPEG",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB",
                                width: 5509,
                                height: 3673
                            }, {
                                name: "sample4",
                                path: "/images/img4.png",
                                format: "GIF",
                                uploaded: "18.02.2017",
                                resolution: "64x64",
                                size: "16kB",
                                width: 102,
                                height: 146
                            }
                        ],
                        folders: [
                            {
                                name: "bmw",
                                path: "images/images_2017/cars/bmw",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "/images/img1.jpg",
                                        format: "JPEG",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB",
                                        width: 5509,
                                        height: 3673
                                    }
                                ],
                                folders: []
                            }, {
                                name: "lada",
                                path: "images/images_2017/cars/lada",
                                files: [
                                    {
                                        name: "sample1",
                                        path: "/images/img1.jpg",
                                        format: "JPEG",
                                        uploaded: "15.02.2017",
                                        resolution: "635x120",
                                        size: "37kB",
                                        width: 5509,
                                        height: 3673
                                    }, {
                                        name: "sample2",
                                        path: "/images/img2.png",
                                        format: "PNG",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB",
                                        width: 147,
                                        height: 146
                                    }, {
                                        name: "sample3",
                                        path: "/images/img3.png",
                                        format: "BMP",
                                        uploaded: "17.02.2017",
                                        resolution: "635x120",
                                        size: "34kB",
                                        width: 32,
                                        height: 29
                                    }, {
                                        name: "sample4",
                                        path: "/images/img4.png",
                                        format: "GIF",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB",
                                        width: 102,
                                        height: 146
                                    }
                                ],
                                folders: [{
                                    name: "kalyna",
                                    path: "images/images_2017/cars/lada/kalyna",
                                    files: [
                                        {
                                            name: "sample2",
                                            path: "/images/img2.png",
                                            format: "PNG",
                                            uploaded: "16.02.2017",
                                            resolution: "635x120",
                                            size: "35kB",
                                            width: 147,
                                            height: 146
                                        }, {
                                            name: "sample3",
                                            path: "/images/img3.png",
                                            format: "BMP",
                                            uploaded: "17.02.2017",
                                            resolution: "635x120",
                                            size: "34kB",
                                            width: 32,
                                            height: 29
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
                                path: "/images/img1.jpg",
                                format: "JPEG",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB",
                                width: 5509,
                                height: 3673
                            }, {
                                name: "sample3",
                                path: "/images/img3.png",
                                format: "BMP",
                                uploaded: "17.02.2017",
                                resolution: "635x120",
                                size: "34kB",
                                width: 32,
                                height: 29
                            }, {
                                name: "sample4",
                                path: "/images/img4.png",
                                format: "GIF",
                                uploaded: "18.02.2017",
                                resolution: "64x64",
                                size: "16kB",
                                width: 102,
                                height: 146
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
                        path: "/images/img1.jpg",
                        format: "JPEG",
                        uploaded: "15.02.2017",
                        resolution: "635x120",
                        size: "37kB",
                        width: 5509,
                        height: 3673
                    }, {
                        name: "sample4",
                        path: "/images/img4.png",
                        format: "GIF",
                        uploaded: "18.02.2017",
                        resolution: "64x64",
                        size: "16kB",
                        width: 102,
                        height: 146
                    }
                ],
                folders: [
                    {
                        name: "summer",
                        path: "images/images_2016/summer",
                        files: [
                            {
                                name: "sample1",
                                path: "/images/img1.jpg",
                                format: "JPEG",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB",
                                width: 5509,
                                height: 3673
                            }, {
                                name: "sample2",
                                path: "/images/img2.png",
                                format: "PNG",
                                uploaded: "16.02.2017",
                                resolution: "635x120",
                                size: "35kB",
                                width: 147,
                                height: 146
                            }
                        ],
                        folders: [
                            {
                                name: "img",
                                path: "images/images_2016/summer/img",
                                files: [
                                    {
                                        name: "sample2",
                                        path: "/images/img2.png",
                                        format: "PNG",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB",
                                        width: 147,
                                        height: 146
                                    }, {
                                        name: "sample3",
                                        path: "/images/img3.png",
                                        format: "BMP",
                                        uploaded: "17.02.2017",
                                        resolution: "635x120",
                                        size: "34kB",
                                        width: 32,
                                        height: 29
                                    }, {
                                        name: "sample4",
                                        path: "/images/img4.png",
                                        format: "GIF",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB",
                                        width: 102,
                                        height: 146
                                    }
                                ],
                                folders: []
                            }, {
                                name: "family",
                                path: "images/images_2016/summer/family",
                                files: [
                                    {
                                        name: "sample2",
                                        path: "/images/img2.png",
                                        format: "PNG",
                                        uploaded: "16.02.2017",
                                        resolution: "635x120",
                                        size: "35kB",
                                        width: 147,
                                        height: 146
                                    }, {
                                        name: "sample4",
                                        path: "/images/img4.png",
                                        format: "GIF",
                                        uploaded: "18.02.2017",
                                        resolution: "64x64",
                                        size: "16kB",
                                        width: 102,
                                        height: 146
                                    }
                                ],
                                folders: [{
                                    name: "photo",
                                    path: "images/images_2016/summer/family/photo",
                                    files: [
                                        {
                                            name: "sample1",
                                            path: "/images/img1.jpg",
                                            format: "JPEG",
                                            uploaded: "15.02.2017",
                                            resolution: "635x120",
                                            size: "37kB",
                                            width: 5509,
                                            height: 3673
                                        }, {
                                            name: "sample2",
                                            path: "/images/img2.png",
                                            format: "PNG",
                                            uploaded: "16.02.2017",
                                            resolution: "635x120",
                                            size: "35kB",
                                            width: 147,
                                            height: 146
                                        }, {
                                            name: "sample3",
                                            path: "/images/img3.png",
                                            format: "BMP",
                                            uploaded: "17.02.2017",
                                            resolution: "635x120",
                                            size: "34kB",
                                            width: 32,
                                            height: 29
                                        }, {
                                            name: "sample4",
                                            path: "/images/img4.png",
                                            format: "GIF",
                                            uploaded: "18.02.2017",
                                            resolution: "64x64",
                                            size: "16kB",
                                            width: 102,
                                            height: 146
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
                                path: "/images/img1.jpg",
                                format: "JPEG",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB",
                                width: 5509,
                                height: 3673
                            }
                        ],
                        folders: []
                    }, {
                        name: "spring",
                        path: "images/images_2016/spring",
                        files: [
                            {
                                name: "sample1",
                                path: "/images/img1.jpg",
                                format: "JPEG",
                                uploaded: "15.02.2017",
                                resolution: "635x120",
                                size: "37kB",
                                width: 5509,
                                height: 3673
                            }, {
                                name: "sample3",
                                path: "/images/img3.png",
                                format: "BMP",
                                uploaded: "17.02.2017",
                                resolution: "635x120",
                                size: "34kB",
                                width: 32,
                                height: 29
                            }, {
                                name: "sample4",
                                path: "/images/img4.png",
                                format: "GIF",
                                uploaded: "18.02.2017",
                                resolution: "64x64",
                                size: "16kB",
                                width: 102,
                                height: 146
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
