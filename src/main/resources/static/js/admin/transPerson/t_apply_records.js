var tabs = document.getElementById('tab').getElementsByTagName('button');
for(var i=0; i<tabs.length; i++) {
    tabs[i].onclick = function () {
        var tabId = this.id;
        //若当前按钮已选中则点击不再触发事件
        if($(this).hasClass('button')) {return};
        AjaxTool.get('transper/getApplyTablesContent', {
                lx: this.id},function (data) {
                if(data.success) {
                console.log(data.content)
                    var str = "";
                    var cons = data.content;
                    for(var i=0;i<cons.length;i++) {
                        str += "<tr>";
                        str += "<td>"+cons[i].jobKind+"</td>";
                        str += "<td><div>"+cons[i].applyDate+"</div></td>";
                        switch(tabId){
                            case "wtj":
                                str += "<td>未提交</td>";
                                str += "<td><div onclick='seeApplyRecordE(\""+ cons[i].id +'\",\"' + tabId +"\")'>查看</div>" +
                                    "<div onclick='submitWtjForm(\""+ cons[i].id +"\")'>提交</div>" +
                                    "<div onclick='deleteWtjCon(\""+ cons[i].id +"\")'>删除</div></td>";
                                break;
                            case "dsp":
                                str += "<td><div>"+cons[i].state+"</div></td>";
                                str += "<td><div onclick='seeApplyRecordNE(\""+ cons[i].id +'\",\"' + tabId +"\")' >查看</div></td>";
                                break;
                            case "ysp":
                                str += "<td><div>"+cons[i].state+"</div></td>";
                                str += "<td><div onclick='seeApplyRecordNE(\""+ cons[i].id +'\",\"' + tabId +"\")'>查看</div>";
                                if (cons[i].state=='已通过') {
                                    str += "<div onclick='printGw(\"" + cons[i].id + '\",\"' + tabId + "\")'>打印</div>";
                                }
                                break;
                            // case "rejected":
                            //     str += "<td><div>"+cons[i].state+"</div></td>";
                            //     str += "<td><div onclick='seeApplyRecordNE(\""+ cons[i].id +'\",\"' + tabId +"\")'>查看</div>" ;
                            //     break;
                            default:
                                break;
                        }
                        str += "</tr>";
                    }
//                    mTable.fnClearTable();
                    var mTable = $('#d-apply-record-table').DataTable();
                    mTable.destroy();
                    $('#d-approve-tbody').html(str);
                    init();
                }
            }
        );
        $(this).removeClass("button-active").addClass("button");
        for(var j=0;j<tabs.length;j++) {
            if(tabs[j] != this) {
                $(tabs[j]).removeClass("button").addClass("button-active");
            }
        }
    }
}

function seeApplyRecordE(id,tabId) {
    AjaxTool.html('transper/sqjlxqE',{id: id},function (html) {
        $('.portlet-body').html(html);
        if(tabId == "yth") {
            $('#splc').attr('style', 'display:block');
        }
        $('#back').data('tabId',tabId);
    });
}

function seeApplyRecordNE(id,tabId) {
    AjaxTool.html('transper/sqjlxqNE',{id: id},function (html) {
        $('.portlet-body').html(html);
        if(tabId == "dsp") {
            $('#tzxq').attr('style','display:none');
        }
        $('#back').data('tabId',tabId);
    });
}

function submitWtjForm(id) {
    AjaxTool.post('transper/commitSaved',{id: id}, function (data) {
            // alert(data.message);
            window.location.reload();
        }
    )
};

function printGw(id,tabId){
    AjaxTool.html('transper/getPrintHtml',{id: id},function (html) {
        $('.portlet-body').html(html);
        $('#back').data('tabId',tabId);
    });
}

function notify(id,tabId){
    // AjaxTool.html('document/notify',{id: id},function (html) {
    //     $('.portlet-body').html(html);
    //     $('#back').data('tabId',tabId);
    // })
}
function deleteWtjCon(id) {
    AjaxTool.post('transper/deleteWtj',{
        id:id},function (data) {
        var table = $('#d-apply-record-table').DataTable();
        alert(data.message);
        if (data.success){
            table.rows('#'+id).remove().draw();
        }
        // window.location.reload();
    })
}

function init() {//dataTable初始化
    mTable=DatatableTool.initDatatable("d-apply-record-table", [ {
        'orderable' : false,
        'targets' : [ 3 ]
    }, {
        "searchable" : false,
        "targets" : [ 3 ]
    }], [ [ 1, "desc" ] ]);
}

jQuery(document).ready(function() {
    init();
});

