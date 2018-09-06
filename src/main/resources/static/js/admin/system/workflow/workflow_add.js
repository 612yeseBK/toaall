//这个js版本不支持箭头函数，所以无法获取vue的实例，我只能退而求其次，曲线救国
new Vue({
    el: '#app',
    data: {
        chooseOne: '',
        true: true,
        username:[''],
        lists:[{id:0001,name:"n1",description:"dede"},{id:0002,name:"n2",description:"dede2"}],
        users:[{id:'0001',name:'张三'}],
        wfTable:[{id:0001,name:"n1",typeName:'人员借调',description:"这是一段描述",candele:false},{id:0002,name:"人员借调",description:"这是一段描述",candele:false}],
        addDialogVisible: false,
        wfDialogVisible: false,
        addUsrDialogVisible:false,
        types:[],
        functions:[],
        wfDetail: {id:'0',name:'',type:'',description:'',candele:'',wfpInfos:[{id:'0',name:'',funcId:[],usersInfos:[]}]}
        //        流程id  流程名         流程所属类别        流程描述   流程节点信息 节点id 节点名  节点人员   人员id 人员姓名name
    },
    mounted: function () {
        this.get_types(this)
        this.my_add_hock()
        // this.get_functions(this)
    },
    methods: {
        get_types: function (me) {
            AjaxTool.post('workf/getTypes',{}, function (data) {
                console.log(data)
                me.types = []
                var keys = Object.keys(data)
                for (var i = 0;i<keys.length;i++){
                    var key = keys[i]
                    me.types.push({value:key,label:data[key]})
                }
                console.log(keys)
                }
            )
        },
        get_AllTypes:function (me) {
            AjaxTool.post('workf/getAllTypes',{}, function (data) {
                    console.log(data)
                    me.types = []
                    var keys = Object.keys(data)
                    for (var i = 0;i<keys.length;i++){
                        var key = keys[i]
                        me.types.push({value:key,label:data[key]})
                    }
                    console.log(keys)
                }
            )
        },
        get_functions: function (me,type) {
            AjaxTool.post('workf/getFucs',{pareName:type}, function (data) {
                    me.functions = data
                    console.log(data)
                }
            )
        },
        my_add: function (me) {
            AjaxTool.post('workf/getAllWfInfo',{}, function (data) {
                me.wfTable = data
                }
            )
        },
        open_wf: function (me) {
            me.get_types(me)
            me.functions = []
            me.wfDetail = {id:'0',name:'',type:'',description:'',wfpInfos:[{id:'0',name:'',funcId:[],usersInfos:[]}]}
            me.username = ['']
            me.wfDialogVisible=true
        },
        addpoint: function (me) {
            me.wfDetail.wfpInfos.push({id:'0',name:'',funcId:[],usersInfos:[]})
            me.username.push('')
        },
        delepoint: function (me,index) {
            me.wfDetail.wfpInfos.splice(index,1)
            me.username.splice(index,1)
        },
        editWf: function (me,index) {
            me.get_AllTypes(me)
            me.username = ['','','','','','','']
            var id = me.wfTable[index].id
            console.log(id)
            AjaxTool.post('workf/getOneWfInfo',{id:id}, function (data) {
                console.log(data)
                    me.wfDetail = data
                }
            )
            me.wfDialogVisible = true
        },
        addwf: function (me) {
            me.get_AllTypes(me)
            console.log(me.wfDetail)
            AjaxTool.post('workf/addOrUpd',{
                wfDetail: JSON.stringify(me.wfDetail)
                }, function (data) {
                if (data.success) {
                    me.$message.success("保存成功")
                    me.wfDialogVisible = false
                    me.my_add_hock()
                } else {
                    me.$message.error("保存失败")
                }
                }
            )
        },
        cancelwf: function (me) {
            // me.wfDetail = {id:'0',name:'',type:'',description:'',wfpInfos:[{id:'0',name:'',funcId:[],funcId2:'',usersInfos:[]}]}
            me.wfDetail = {id:'0',name:'',type:'',description:'',wfpInfos:[{id:'0',name:'',funcId:[],usersInfos:[]}]}
            me.wfDialogVisible = false
        },
        delUser: function (me,index1,index2) {
            me.wfDetail.wfpInfos[index1].usersInfos.splice(index2,1)
        },
        addUsrDia: function (me,index) {
            me.addUsrDialogVisible = true
        },
        addUsr2Info: function (me,index) {
            //向后台请求一个username这个人对应的user对象，只需要id和name
            AjaxTool.post('workf/getUserFromUserName',{username:me.username[index]}, function (data) {
                    if (data.result){
                        var temp = me.wfDetail.wfpInfos[index].usersInfos
                        for(var i =0; i<temp.length;i++){
                            if (temp[i].id == data.idName.id){
                                Toast.show("该用户已经添加");
                                return;
                            }
                        }
                        me.wfDetail.wfpInfos[index].usersInfos.push(data.idName)
                        me.username[index] = ''
                        console.log(me.wfDetail)
                    } else {
                        Toast.show("找不到该用户，请重新输入");
                    }
                }
            )
            me.addUsrDialogVisible = false
        },
        abort: function (me,index) {
            var id = me.wfTable[index].id
            console.log(id)
            AjaxTool.post('workf/abortWf',{id:id}, function (data) {
                console.log(data)
                    if (data.success){
                        Toast.show("已成功弃用");
                        me.my_add_hock()
                    } else{
                        Toast.show("异常状况");
                    }
                }
            )
        },
        reuse: function (me,index) {
            var id = me.wfTable[index].id
            console.log(id)
            AjaxTool.post('workf/reuse',{id:id}, function (data) {
                    console.log(data)
                    if (data.success){
                        Toast.show("已成功启用");
                        me.my_add_hock()
                    } else{
                        Toast.show("异常状况");
                    }
                }
            )
        },
        dele: function (me,index) {
            var id = me.wfTable[index].id
            console.log(id)
            swal(
                {title:"您确定要删除这条数据吗",
                    text:"删除后将无法恢复，请谨慎操作！",
                    type:"warning",
                    showCancelButton:true,
                    confirmButtonColor:"#DD6B55",
                    confirmButtonText:"确定删除！",
                    cancelButtonText:"取消",
                    closeOnConfirm:false,
                    closeOnCancel:false
                },
                function(isConfirm)
                {
                    if(isConfirm)
                    {
                        swal.close()
                        AjaxTool.post('workf/deleWf',{id:id}, function (data) {
                                console.log(data)
                                if (data.success){
                                    Toast.show("已成功删除");
                                    me.my_add_hock()
                                } else{
                                    Toast.show("异常状况");
                                }
                            }
                        )
                    }
                    else{
                        swal.close()
                    }
                }
            )

        },
        my_add_hock: function () {
            this.my_add(this)
        },
        delepoint_hock: function (index) {
            this.delepoint(this,index)
        },
        addpoint_hock: function () {
            this.addpoint(this)
        },
        editWf_hock: function (index) {
            this.editWf(this,index)
        },
        addwf_hock: function () {
            this.addwf(this)
        },
        cancelwf_hock:function () {
            this.cancelwf(this)
        },
        delUser_hock:function (index1,index2) {
            this.delUser(this,index1,index2)
        },
        open_wf_hock: function () {
            this.open_wf(this)
        },
        addInUsers_hock: function (info) {
            console.log(info)
            console.log(this.chooseOne)
        },
        addUsrDia_hock:function (index) {
            this.addUsrDia(this,index)
            console.log("======")
            console.log(index)
        },
        addUsr2Info_hock: function (index) {
            this.addUsr2Info(this,index)
        },
        abort_hock: function (index) {
            this.abort(this,index)
        },
        reuse_hock: function (index) {
            this.reuse(this,index)
        },
        dele_hock: function (index) {
            this.dele(this,index)
        }
    },
    watch: {
        'wfDetail.type':function (type) {
            if (type != '') {
                this.get_functions(this, type)
            } else {
                this.types = []
            }
            console.log(type)
        }
    }
})