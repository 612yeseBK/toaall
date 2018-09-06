(function () {
    function createDelete() {
        var span = document.createElement('span');
        span.innerHTML = 'x';
        span.setAttribute('style','margin-left:10px;color:red;font-weight:bold;cursor:pointer');
        span.setAttribute('class','delete');
        return span;
    }
    function deletefj() {
        var deletes = document.getElementsByClassName('delete');
        for(var i=0;i<deletes.length;i++) {
            deletes[i].onclick = function () {
                ids.splice(ids.indexOf(this.id),1);
                this.parentNode.setAttribute('style','display:none;');
            }
        }
    }

    var ids = [];
    var s1 = createDelete();
    var fjlbLi = $('#fjlb li');
    fjlbLi.append(s1);
    for (var i=0;i<fjlbLi.length;i++) {
        ids.push(fjlbLi[i].id);
    }
    deletefj();


    $('#uploadFile').click(function () {
        DatatableTool.modalShow("#upload-modal", "#fileUploadForm");
        var uploader = $("#fileUploadForm").FileUpload({
            url: "traveling/uploadTravelingAttachment",
            isMultiFile: true,
        });
        uploader.done(function(data) {
            ids.push(data.result.id);
            var li = document.createElement('li');
            var s2 = createDelete();
            s2.setAttribute('id',data.result.id);
            li.innerHTML = data.result.name;
            li.appendChild(s2);
            $('#fjlb').append(li);
            deletefj();
        });
    });


    //附件查看
    var n = 1;
    $('#fjck').click(
        function () {
            var attachList = JSON.parse($('#attachList').val());
            var travelingId = $('#travelingId').val();
            if(attachList.length == 0) {
                alert('无附件');
            } else {
                if(n%2==1) {
                    for (var i = 0; i < attachList.length; i++) {
                        var li = document.createElement('li');
                        var div = document.createElement('div');
                        div.innerHTML = attachList[i].name;
                        div.setAttribute('style', 'cursor:pointer;');
                        div.setAttribute('class', 'attachment');
                        li.appendChild(div);
                        li.setAttribute('class', 'attList')
                        this.parentNode.appendChild(li);
                        div.id = attachList[i].id;              //将变量保存给对象,避免循环闭包
                        div.onclick = function () {
                            window.location = "traveling/travelingAttachmentDownload?attachmentId=" + this.id+"&travelingId="+travelingId;
                        }
                    }
                }
                else {
                    $('.attList').hide();
                }
                n += 1;
            }
        }
    );

    //判断两个引用该js的文件，如果是可编辑页面调用该方法
    if(document.getElementById("sex")){
        var selected = $('#xb').val();
        var options = document.getElementById("sex").options;
        for(i = 0; i < options.length; i++){
            if(selected == options[i].value)
                options[i].selected = true;
        }

        $('#dateFrom').datetimepicker({
            endDate: new Date($('#dateTo').val().replace(/\//g,",")),
            format:'yyyy/mm/dd',
            language: 'zh-CN',
            autoclose: 1,
            startView: 2,
            minView: 2
        }).on('changeDate',function (ev) {
            var startTime = $('#dateFrom').val().replace(/\//g,",");
            $('#dateTo').datetimepicker('setStartDate',new Date(startTime));
        });

        $('#dateTo').datetimepicker({
            startDate: new Date($('#dateFrom').val().replace(/\//g,",")),
            format:'yyyy/mm/dd',
            language: 'zh-CN',
            autoclose: 1,
            startView: 2,
            minView: 2
        }).on('changeDate',function (ev) {
            var endTime = $('#dateTo').val().replace(/\//g,",");
            $('#dateFrom').datetimepicker('setEndDate',new Date(endTime));
        });

    }


    //提交
    $('#tj').click(function() {
        var formContents = document.getElementsByClassName('form-content');
        for(var i=0; i<formContents.length; i++ ) {
            if(/^\s*$/.test(formContents[i].value)) {
                alert('请填写完整信息!');
                return false;
            }
        }
        if(validator.form()){
            AjaxTool.post('traveling/saveTraveling', $('#t_apply_form').serialize()+"&bczl="+this.id+"&fileId="+ids, function (data) {
                    alert(data.message);
                    if(data.success) toSqjl();
                }
            )
        }
    });

    //保存
    $('#bc').click(function() {
        var formContents = document.getElementsByClassName('form-content');
        for(var i=0; i<formContents.length; i++ ) {
            if(/^\s*$/.test(formContents[i].value)) {
                alert('请填写完整信息!');
                return false;
            }
        }
        if(validator.form()){
            AjaxTool.post('traveling/saveTraveling', $('#t_apply_form').serialize()+"&bczl="+this.id+"&fileId="+ids, function (data) {
                    alert(data.message);
                    if(data.success) toSqjl();
                }
            )
        }
    });


    function toSqjl() {
        AjaxTool.getHtml('traveling/sqjl',function (html) {
            $('.page-content').html(html);
        });
    }


    $('#back').click(function () {
        var tabId = ($('#back').data('tabId'));
        AjaxTool.getHtml('traveling/sqjl',function (html) {
            $('.page-content').html(html);
            $('#'+tabId).trigger('click');
        });
    });

    // var validator = $('#t_apply_form').validate({
    //     errorElement: 'span', //default input error message container
    //     errorClass: 'error-tips', // default input error message class
    //     rules: {
    //         name:{
    //             maxlength: 5
    //         },
    //         sex:{
    //             required: true
    //         },
    //         birth:{
    //             maxlength: 10
    //         },
    //         staffId:{
    //             maxlength: 10
    //         },
    //         position:{
    //             maxlength: 10
    //         },
    //         workUnit: {
    //             maxlength: 20
    //         },
    //         kssj:{
    //             dateISO: true
    //         },
    //         jssj:{
    //             dateISO: true
    //         },
    //         reasonNumber:{
    //             maxlength: 250
    //         }
    //     },
    //     messages: {
    //         name: {
    //             maxlength: "不超过5个字"
    //         },
    //         sex:{
    //             maxlength: "请选择性别！"
    //         },
    //         birth:{
    //             maxlength: "日期格式不正确"
    //         },
    //         staffId:{
    //             maxlength: "不超过10个字"
    //         },
    //         position:{
    //             maxlength: "不超过10个字"
    //         },
    //         workUnit: {
    //             maxlength: "不超过20个字"
    //         },
    //         kssj:{
    //             dateISO: "日期格式不正确"
    //         },
    //         jssj:{
    //             dateISO: "日期格式不正确"
    //         },
    //         reasonNumber:{
    //             maxlength: "不超过250个字"
    //         }
    //     }
    // });

})();


