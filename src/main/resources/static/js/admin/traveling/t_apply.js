$(document).ready(function () {
    var ids = [];
    $('#uploadFile').click(function () {
        DatatableTool.modalShow("#upload-modal", "#fileUploadForm");
        var uploader = $("#fileUploadForm").FileUpload({
            url: "traveling/uploadTravelingAttachment",
            isMultiFile: true
        });
        uploader.done(function(data) {
            ids.push(data.result.id);
            var li = document.createElement('li');
            var span = document.createElement('span');
            span.innerHTML = 'x';
            span.setAttribute('style','margin-left:10px;color:red;font-weight:bold;cursor:pointer');
            span.setAttribute('class','delete');
            span.setAttribute('id',data.result.id);
            li.innerHTML = data.result.name;
            li.appendChild(span);
            $('#fjlb').append(li);

            var deletes = document.getElementsByClassName('delete');
            for(var i=0;i<deletes.length;i++) {
                deletes[i].onclick = function () {
                    ids.splice(ids.indexOf(this.id),1);
                    this.parentNode.setAttribute('style','display:none;');
                }
            }
        });
    });



    var buttons = document.getElementById('saveCon').getElementsByTagName('button');
    for(var i=0; i<buttons.length;i++) {
        buttons[i].onclick= function() {
            var formContents = document.getElementsByClassName('form-content');
            for(var i=0; i<formContents.length; i++ ) {
                if(/^\s*$/.test(formContents[i].value)) {
                    alert('请填写完整信息!');
                    return false;
                }
            }
            if(validator.form()) {
                AjaxTool.post('traveling/saveTraveling', $('#t_apply_form').serialize()+"&bczl="+this.id+"&fileId="+ids, function (data) {
                        alert(data.message);
                        if(data.success) toSqjl();
                    }
                )
            }
        };
    }

    function toSqjl() {
        AjaxTool.getHtml('traveling/sqjl',function (html) {
            $('.page-content').html(html);
        });
    }

    $('#dateFrom').datetimepicker({
        format:'yyyy/mm/dd',
        language: 'zh-CN',
        startDate: new Date(),
        autoclose: 1,
        startView: 2,
        minView: 2
    }).on('changeDate',function (ev) {
        var startTime = $('#dateFrom').val().replace(/\//g,",");
        $('#dateTo').datetimepicker('setStartDate',new Date(startTime));
    });

    $('#dateTo').datetimepicker({
        format:'yyyy/mm/dd',
        language: 'zh-CN',
        autoclose: 1,
        startView: 2,
        minView: 2
    }).on('changeDate',function (ev) {
        var endTime = $('#dateTo').val().replace(/\//g,",");
        $('#dateFrom').datetimepicker('setEndDate',new Date(endTime));
    });

    // jQuery.validator.addMethod("isBirth", function (value) {
    //
    //
    //     var reg = new RegExp(/^\d{4}-((0?[1-9])|(1[0-2]))-((0?[1-9])|([12]\d)|(3[01]))$/)
    //
    //
    //     return this.optional(reg.test(value));
    //
    // }, "日期格式不对!");

    var validator = $('#t_apply_form').validate({
        errorElement: 'span', //default input error message container
        errorClass: 'error-tips', // default input error message class
        rules: {
            name:{
                maxlength: 5
            },
            sex:{
                required: true
            },
            birth:{
                isBirth: true
            },
            staffId:{
                maxlength: 10
            },
            position:{
                maxlength: 10
            },
            workUnit: {
                maxlength: 20
            },
            kssj:{
                dateISO: true
            },
            jssj:{
                dateISO: true
            },
            reasonNumber:{
                maxlength: 250
            }
        },
        messages: {
            name: {
                maxlength: "不超过5个字"
            },
            sex:{
                maxlength: "请选择性别！"
            },
            staffId:{
                maxlength: "不超过10个字"
            },
            position:{
                maxlength: "不超过10个字"
            },
            workUnit: {
                maxlength: "不超过20个字"
            },
            kssj:{
                dateISO: "日期格式不正确"
            },
            jssj:{
                dateISO: "日期格式不正确"
            },
            reasonNumber:{
                maxlength: "不超过250个字"
            }
        }
    });
});