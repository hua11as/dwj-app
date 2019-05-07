$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + '/openprize/video/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 20, key: true },
            { label: '播放员编号', name: 'deingerNum', sortable: false, width: 30 },
            { label: '播放总时长(秒)', name: 'totalPlayTimes', sortable: false, width: 30 },
            { label: '闲家牌', name: 'playerPoint', width: 30 },
            { label: '闲家牌颜色', name: 'playerPointColor', width: 30 },
            { label: '庄家牌', name: 'bankerPoint', width: 30 },
            { label: '庄家牌颜色', name: 'bankerPointColor', width: 30 },
            { label: '开奖结果', name: 'resultSign', width: 15, formatter: function(value, options, row){
                return value === 2 ?
                    '<span class="label label-success">庄胜</span>' : value==1?
                    '<span class="label label-danger">闲胜</span>': '<span class="label label-danger">和</span>';
            }},
            { label: '投注时长(秒)', name: 'orderTimes', width: 25 },
            { label: '计算时长(秒)', name: 'calOrderTimes', width: 25 },
            { label: '发牌时长(秒)', name: 'playTimes', width: 25 },
            { label: '开奖时长(秒)', name: 'showResultTimes', width: 25 },
            { label: '状态', name: 'status', width: 15, formatter: function(value, options, row){
                return value === 0 ?
                    '<span class="label label-success">有效</span>' :
                    '<span class="label label-danger">无效</span>';
            }},
            { label: '视频地址', name: 'linkAdress', width: 60 },
            { label: '备注', name: 'remark', width: 60 },


        ],
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
            id: null,
            deingerNum: null
        },
        showList: true,
        title: null,
        video: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.video = {};
        },
        update: function () {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }

            $.get(baseURL + "/openprize/video/info/"+id, function(r){
                vm.showList = false;
                vm.title = "修改";
                vm.title = "修改";
                vm.video = r.video;
            });
        },
        del: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "/openprize/video/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(index){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function (event) {
            var url = vm.video.id == null ? "/openprize/video/save" : "/openprize/video/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.video),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'id': vm.q.id, 'deingerNum': vm.q.deingerNum},
                page:page
            }).trigger("reloadGrid");
        },
        upload: function (event) {
            var url = "/openprize/video/upload";
            var formData = new FormData();
            var fileObj = document.getElementById("uploadFile").files[0];
            formData.append("file", fileObj);
            $.ajax({
                type: "POST",
                url: baseURL + url,
                // contentType: "application/json",
                data: formData,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function(r){
                    if(r.code === 0){
                        alert('批量上传成功');
                        vm.reload();
                    }else{
                        alert(r.msg);
                    }
                    $('#uploadFile').val('');
                }
            });
        },
        refreshPair: function (event) {
            var url = "/openprize/video/refreshPointPair";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.video),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        }
    }
});