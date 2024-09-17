$(function () {
    echarts_22();
    echarts_1();
    echarts_2();
    echarts_3();
    echarts_4();
    function echarts_22() {
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('echart22'));
    
        var option1 = {
            tooltip: {
                trigger: 'axis',
                axisPointer: { type: 'shadow' }
            },
            grid: {
                left: '0%',
                top: '5px',
                right: '0%',
                bottom: '0px',
                containLabel: true
            },
            xAxis: [{
                type: 'time',
                axisLine: {
                    show: true,
                    lineStyle: {
                        color: "rgba(255,255,255,.1)",
                        width: 1,
                        type: "solid"
                    },
                },
                axisTick: {
                    show: false,
                },
                axisLabel: {
                    show: true,
                    formatter: function (value) {
                        return echarts.format.formatTime('MM-dd hh:mm:ss', value);
                    },
                    textStyle: {
                        color: "rgba(255,255,255,.6)",
                        fontSize: '12',
                    },
                },
            }],
            yAxis: [{
                type: 'value',
                axisLabel: {
                    formatter: '{value} °C',
                    show: true,
                    textStyle: {
                        color: "rgba(255,255,255,.6)",
                        fontSize: '12',
                    },
                },
                axisTick: {
                    show: false,
                },
                axisLine: {
                    show: true,
                    lineStyle: {
                        color: "rgba(255,255,255,.1)",
                        width: 1,
                        type: "solid"
                    },
                },
                splitLine: {
                    lineStyle: {
                        color: "rgba(255,255,255,.1)",
                    }
                }
            }],
            series: [
                {
                    type: 'line',
                    data: [
                        {value: [new Date(), 2]},
                    ],
                    itemStyle: {
                        normal: {
                            color: '#37a3ff',
                            opacity: 1,
                            barBorderRadius: 3,
                        }
                    }
                }
            ]
        };
    
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option1);
    
        // 模拟数据更新
        function updateChartData() {
            var now = new Date();
            var value = Math.floor(20 + (Math.random() * 0.1 - 0.55));
            var newData = {value: [now, value]};
            var data = myChart.getOption().series[0].data;
            data.push(newData);
            if (data.length > 20) { 
                data.shift();
            }
            myChart.setOption({
                series: [{
                    data: data
                }]
            });
        }
    
        setInterval(updateChartData, 1000); 
    
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    }
    
    
    

    function echarts_1() {
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('feng01'));
        var gauge_value = 66;

        option = {
            title: {
                x: "center",
                bottom: 10,
                //text: '风向',
                subtext: '风向',
                textStyle: {
                    fontWeight: 'normal',
                    fontSize: 18,
                    color: "#fff"
                },
            },
            tooltip: {
                show: true,


            },
            series: [
                {
                    type: 'gauge',
                    radius: '60%',
                    center: ['50%', '55%'],
                    splitNumber: 7, //刻度数量
                    min: 0,
                    max: 70,
                    startAngle: 225,
                    endAngle: -45,
                    axisLine: {
                        show: true,
                        lineStyle: {
                            width: 2,
                            shadowBlur: 0,
                            color: [
                                [1, '#8f8f8f']
                            ]
                        }
                    },
                    axisTick: {
                        show: false,
                        lineStyle: {
                            color: '#8f8f8f',
                            width: 1
                        },
                        length: -5,
                        splitNumber: 7
                    },
                    splitLine: {
                        show: true,
                        length: -5,
                        lineStyle: {
                            color: "rgba(255,255,255,.2)",
                        }
                    },
                    axisLabel: {
                        distance: -25,

                        formatter: function (e) {
                            switch (e + "") {
                                case "0":
                                    return "北风";
                                case "10":
                                    return "东北风";

                                case "20":
                                    return "东风";

                                case "30":
                                    return "东南风";

                                case "40":
                                    return "南风";

                                case "50":
                                    return "西南风";

                                case "60":
                                    return "西风";

                                case "70":
                                    return "西北风";

                                default:
                                    return e;
                            }
                        },
                        textStyle: {
                            fontSize: 10,
                            color: "rgba(255,255,255,.6)",
                        }

                    },
                    pointer: { //仪表盘指针
                        show: 0
                    },
                    detail: {
                        show: false
                    },
                    data: [{
                        name: "",
                        value: 100
                    }]
                },

                {
                    name: '风向',
                    type: 'gauge',
                    startAngle: 225,
                    endAngle: -45,
                    radius: '56%',
                    center: ['50%', '55%'], // 默认全局居中  

                    min: 0,
                    max: 100,

                    axisLine: {
                        show: false,
                        lineStyle: {
                            width: 5,
                            shadowBlur: 0,
                            color: [

                                [1, '#82b440']
                            ]
                        }
                    },
                    axisTick: {
                        show: false,

                    },
                    splitLine: {
                        show: false,
                        length: 20,

                    },

                    axisLabel: {
                        show: false
                    },
                    pointer: {
                        show: true,
                        length: '65%',
                        width: 4
                    },
                    detail: {
                        show: false,
                        offsetCenter: [0, 0],
                        textStyle: {
                            fontSize: 10
                        }
                    },
                    itemStyle: {
                        normal: {
                            color: "#82b440",

                        }
                    },
                    data: [{
                        name: "",
                        value: Math.floor(gauge_value)
                    }]
                }]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    }
    function echarts_2() {
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('feng02'));
        var gauge_value = 3;
        option = {
            title: {
                x: "center",
                bottom: 0,
                text: gauge_value,
                subtext: '风速km/h',
                textStyle: {
                    fontWeight: 'normal',
                    fontSize: 18,
                    color: "#fff"
                },
            },
            tooltip: {
                show: true,


            },
            series: [
                {
                    type: 'gauge',
                    radius: '60%',
                    center: ['50%', '55%'],
                    splitNumber: 7, //刻度数量
                    min: 0,
                    max: 70,
                    startAngle: 225,
                    endAngle: -45,
                    axisLine: {
                        show: true,
                        lineStyle: {
                            width: 2,
                            shadowBlur: 0,
                            color: [
                                [1, '#8f8f8f']
                            ]
                        }
                    },
                    axisTick: {
                        show: false,
                        lineStyle: {
                            color: '#8f8f8f',
                            width: 1
                        },
                        length: -5,
                        splitNumber: 7
                    },
                    splitLine: {
                        show: true,
                        length: -5,
                        lineStyle: {
                            color: "rgba(255,255,255,.2)",
                        }
                    },
                    axisLabel: {
                        distance: -15,

                        textStyle: {
                            fontSize: 10,
                            color: "rgba(255,255,255,.6)",
                        }

                    },
                    pointer: { //仪表盘指针
                        show: 0
                    },
                    detail: {
                        show: false
                    },
                    data: [{
                        name: "",
                        value: 100
                    }]
                },

                {
                    name: '风向',
                    type: 'gauge',
                    startAngle: 225,
                    endAngle: -45,
                    radius: '56%',
                    center: ['50%', '55%'], // 默认全局居中  

                    min: 0,
                    max: 70,

                    axisLine: {
                        show: false,
                        lineStyle: {
                            width: 5,
                            shadowBlur: 0,
                            color: [

                                [1, '#2ea7ff']
                            ]
                        }
                    },
                    axisTick: {
                        show: false,

                    },
                    splitLine: {
                        show: false,
                        length: 20,

                    },

                    axisLabel: {
                        show: false
                    },
                    pointer: {
                        show: true,
                        length: '65%',
                        width: 4
                    },
                    detail: {
                        show: false,
                        offsetCenter: [0, 0],
                        textStyle: {
                            fontSize: 10
                        }
                    },
                    itemStyle: {
                        normal: {
                            color: "#2ea7ff",

                        }
                    },
                    data: [{
                        name: "",
                        value: Math.floor(gauge_value)
                    }]
                }]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    }
    function echarts_3() {
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('feng03'));
        var gauge_value = 0;

        option = {
            title: {
                x: "center",
                bottom: 0,
                text: gauge_value,
                subtext: '降雨量ML',
                textStyle: {
                    fontWeight: 'normal',
                    fontSize: 18,
                    color: "#fff"
                },
            },
            tooltip: {
                show: true,


            },
            series: [
                {
                    type: 'gauge',
                    radius: '60%',
                    center: ['50%', '55%'],
                    splitNumber: 10, //刻度数量
                    min: 0,
                    max: 100,
                    startAngle: 225,
                    endAngle: -45,
                    axisLine: {
                        show: true,
                        lineStyle: {
                            width: 2,
                            shadowBlur: 0,
                            color: [
                                [1, '#8f8f8f']
                            ]
                        }
                    },
                    axisTick: {
                        show: false,
                        lineStyle: {
                            color: "rgba(255,255,255,.1)",
                            width: 1
                        },
                        length: -5,
                        splitNumber: 7
                    },
                    splitLine: {
                        show: true,
                        length: -5,
                        lineStyle: {
                            color: "rgba(255,255,255,.2)",
                        }
                    },
                    axisLabel: {
                        distance: -15,
                        textStyle: {
                            fontSize: 10,
                            color: "rgba(255,255,255,.6)",
                        }

                    },
                    pointer: { //仪表盘指针
                        show: 0
                    },
                    detail: {
                        show: false
                    },
                    data: [{
                        name: "",
                        value: 100
                    }]
                },

                {
                    name: '风向',
                    type: 'gauge',
                    startAngle: 225,
                    endAngle: -45,
                    radius: '56%',
                    center: ['50%', '55%'], // 默认全局居中  

                    min: 0,
                    max: 100,

                    axisLine: {
                        show: false,
                        lineStyle: {
                            width: 5,
                            shadowBlur: 0,
                            color: [

                                [1, '#ff7109']
                            ]
                        }
                    },
                    axisTick: {
                        show: false,

                    },
                    splitLine: {
                        show: false,
                        length: 20,

                    },

                    axisLabel: {
                        show: false
                    },
                    pointer: {
                        show: true,
                        length: '65%',
                        width: 4
                    },
                    detail: {
                        show: false,
                        offsetCenter: [0, 0],
                        textStyle: {
                            fontSize: 10
                        }
                    },
                    itemStyle: {
                        normal: {
                            color: "#ff7109",

                        }
                    },
                    data: [{
                        name: "",
                        value: Math.floor(gauge_value)
                    }]
                }]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    }
    function echarts_4() {
        var myChart = echarts.init(document.getElementById('echart4'));
    
        var base = +new Date();  // 获取当前时间
        var oneSecond = 2000;   
        var data = [[base, 2 + Math.random()]];  // 初始摄像头CPU占用大约在2%左右
        var data2 = [[base, 45 + Math.random() * 10]];  // 初始算法CPU占用在45%左右，波动范围小
    
        var option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: { type: 'shadow' },
            }, 
            grid: {
                top: "20%",
                right: "50",
                bottom: "20",
                left: "30",
            },
            legend: {
                data: ['摄像头CPU', '算法CPU'],
                right: 'center', 
                textStyle: {
                    color: "rgba(255,255,255,.5)"
                },
                itemWidth: 12,
                itemHeight: 10,
            },
            xAxis: [
                {
                    type: "time",
                    axisLabel: {
                        textStyle: {
                            fontSize: 14,
                            color: "rgba(255,255,255,.5)",
                        },
                        formatter: function (value) {
                            return echarts.format.formatTime('hh:mm:ss', value);
                        }
                    },
                    splitLine: {
                        show: false,
                    },
                },
            ],
            yAxis: [
                {
                    type: "value",
                    max: 100,
                    axisLabel: {
                        show: true,
                        fontSize: 14,
                        color: "rgba(255,255,255,.5)"
                    },
                    axisLine: { show: false },
                    splitLine: { show: false },
                },
            ],
            series: [
                {
                    name: "摄像头CPU",
                    type: "line",
                    data: data,
                    smooth: true,
                    symbolSize: 8,
                    itemStyle: {
                        color: '#248ff7'
                    },
                },
                {
                    name: "算法CPU",
                    type: "line",
                    data: data2,
                    smooth: true,
                    symbolSize: 8,
                    itemStyle: {
                        color: '#f5804d'
                    },
                },
            ]
        };
    
        myChart.setOption(option);
        window.addEventListener("resize", function () {
            myChart.resize();
        });
    
        setInterval(function () {
            var now = new Date(base += oneSecond);
            var newCpu = 2 + Math.random();  // 摄像头CPU占用波动小
            var newCpu2 = 45 + Math.random() * 10;  // 算法CPU占用波动在45%左右
    
            data.push([now, newCpu]);
            data2.push([now, newCpu2]);
    
            if (data.length > 10) { // 减少数据点到10个
                data.shift();
                data2.shift();
            }
    
            myChart.setOption({
                series: [
                    { data: data },
                    { data: data2 }
                ]
            });
        }, 2000);  
    }
    
    
    
    
    

})

