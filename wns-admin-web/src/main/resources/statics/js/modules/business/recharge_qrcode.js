$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'recharge/qrcode/list',
        datatype: "json",
        colModel: [
            {label: 'ID', name: 'id', width: 30, key: true},
            {label: '二维码地址', name: 'qrCode', width: 60},
            {label: '充值金额（元）', name: 'amount', width: 60},
            {label: '实际充值金额（元）', name: 'realAmount', width: 60},
            {
                label: '状态', name: 'status', width: 60, formatter: function (value, options, row) {
                    let array = ['未使用', '使用中'];
                    return array[value];
                }
            },
            {label: '绑定用户id', name: 'bindUserId', width: 40},
            {label: '绑定用户昵称', name: 'bindNickName', width: 40},
            {label: '绑定时间', name: 'bindTime', width: 60},
            {label: '创建时间', name: 'createTime', width: 60}
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        q: {
            amount: '',
            nickName: ''
        },
        showList: true,
        title: null,
        qrcode: {},
        amountList: [{name: '请选择充值金额', value: ''}],
    },
    created: function () {
        this.getAmountList();
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'amount': vm.q.amount, 'nickName': vm.q.nickName},
                page: page
            }).trigger("reloadGrid");
        },
        getAmountList: function () {
            let _this = this;
            let url = "recharge/qrcode/amountList";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                async: false,
                success: function (r) {
                    if (r.code === 0) {
                        $.each(r.amountList, function (i) {
                            _this.amountList[i + 1] = {name: r.amountList[i] + '元', value: r.amountList[i]};
                        });
                    }
                }
            });

        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.qrcode = {};
            $("#file").val("");
            $("#imageShow").hide();
        },
        del: function (event) {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录（仅删除未使用二维码）？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "/recharge/qrcode/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('操作成功', function (index) {
                                vm.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function (event) {
            var url = "/recharge/qrcode/save";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.qrcode),
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
        },
        upload: function (event) {
            var url = "file/upload";
            var formData = new FormData();
            var fileObj = document.getElementById("file").files[0];
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
                success: function (r) {
                    if (r.code === 0) {
                        vm.qrcode.qrCode = r.path;
                        $("#imageShow").attr("src", r.path);
                        $("#imageShow").show();
                    } else {
                        alert(r.msg);
                        $("#image").val("");
                        $("#imageShow").hide();
                    }
                }
            });
        },
        sync: function () {
            confirm('确定要同步二维码（请确保暂无使用中二维码记录，以及新二维码文件已经上传至服务器）？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "/recharge/qrcode/syncQrcode",
                    contentType: "application/json",
                    success: function (r) {
                        if (r.code == 0) {
                            alert('操作成功\<br\>' + r.msg, function (index) {
                                vm.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
    }
});