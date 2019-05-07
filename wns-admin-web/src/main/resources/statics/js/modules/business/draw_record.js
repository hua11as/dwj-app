var page = {
    url: baseURL + 'drawRecord/list',
    colModel: [
        { label: '开奖期号', name: 'awardPeriod', width: 80, sortable: false, key: true },
        { label: '开始时间', name: 'startOrderTime', width: 80, sortable: false },
        { label: '结束时间', name: 'endShowResultTime', width: 80, sortable: false },
        { label: '中奖结果', name: 'drawResult', width: 50, sortable: false,
            formatter: function (value, options, row) {
                // 中奖结果 1 闲胜 2庄胜，3 和
                var values = ['--', '闲胜', '庄胜', '和'];
                return values[value];
        }},
        { label: '开奖视频', name: 'awardVideo', width: 60, sortable: false },
        { label: '下注金额(总)', name: 'betAmount', width: 60, sortable: false },
        { label: '下注金额(庄)', name: 'betAmount1', width: 60, sortable: false },
        { label: '下注金额(闲)', name: 'betAmount2', width: 60, sortable: false },
        { label: '下注金额(和)', name: 'betAmount3', width: 60, sortable: false },
        { label: '下注金额(庄对)', name: 'betAmount4', width: 60, sortable: false },
        { label: '下注金额(闲对)', name: 'betAmount5', width: 60, sortable: false },
        { label: '强制开和', name: 'forceTie', width: 40, sortable: false,
            formatter: function (value, options, row) {
                // 是否强制开和 0：否 1：是
                var values = ['否', '是'];
                return values[value];
        }},
        // ,
        // { label: '状态', name: 'status', width: 30, sortable: false,
        //     formatter: function (value, options, row) {
        //         // 0:进行中 1：已经结束
        //         var values = ['进行中', '已结束'];
        //         return values[value];
        //     } }
    ],
};

$(function () {
    $("#jqGrid").jqGrid({
        url: page.url,
        datatype: "json",
        colModel: page.colModel,
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        postData: {awardPeriod: getParams("awardPeriod")},
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });
});

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            awardPeriod: getParams("awardPeriod")
        },
        showList: true,
        record: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'awardPeriod': vm.q.awardPeriod},
                page:page
            }).trigger("reloadGrid");
        },
        forceTie: function() {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var rowData = $("#jqGrid").jqGrid('getRowData',id);
            if (rowData['awardVideo']) {
                alert('已生成开奖视频，不能强制开和处理');
                return;
            }
            if ('是' === rowData['forceTie']) {
                alert('已为强制开和，无需强制开和处理');
                return;
            }

            $.ajax({
                type: "POST",
                url: baseURL + "/drawRecord/forceTie",
                contentType: "application/json",
                data: id,
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        }
    }
});