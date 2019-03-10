<%@ page import="com.example.ddbx.tt.data.TtMap" %>
<%@ page import="com.example.ddbx.tt.tool.Tools" %><%--
  Created by IntelliJ IDEA.
  User: 86176
  Date: 2019/3/6
  Time: 13:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    TtMap erp_result=(TtMap) request.getAttribute("erp_result");
    TtMap result_value=new TtMap();
    if(!Tools.myIsNull(erp_result.get("result_value"))){
        result_value=Tools.jsonDeCode_mp(erp_result.get("result_value"));
    }
%>
<div class="text-primary"><em>通融审核：</em>
    <div class="big-conte" style="display: block;">
        <strong style="margin-left: 10px;"><i>处理信息：</i></strong><br>
        <div class="task_margin ng-scope">
            <form name="modalForm" class="form-horizontal ng-pristine ng-valid ng-scope">
                <div class="form-group ng-scope">
                    <label class="col-sm-2 control-label">审核结果：</label>
                    <div class="col-sm-8">
                        <label class="radio-inline">
                            <input type="radio" value="1" name="state_code">通融通过
                        </label>
                        <label class="radio-inline">
                            <input type="radio" value="2" name="state_code">通融不通过
                        </label>
                        <label class="radio-inline">
                            <input type="radio" value="3" name="state_code">回退补件
                        </label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">原因说明：</label>
                    <div class="col-sm-8">
                        <textarea rows="3" id="yxsm" name="yxsm" class="form-control"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">申请通融原因：</label>
                    <div class="col-sm-8">
                        <textarea rows="3" id="tr_msg" name="tr_msg" class="form-control" ><%=result_value.get("tr_msg")%></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">通融资料：</label>
                    <div class="col-sm-10">
                        <div class="row inline-from gallerys">
                            <input id="imgstep1_5ss" name="imgstep1_5ss" value="<%=result_value.get("imgstep1_5ss")%>"  type="hidden" />
                            <%
                                String[] ssImgs1_1 = { //设置已有值
                                        !Tools.myIsNull(result_value.get("imgstep1_5ss")) ? result_value.get("imgstep1_5ss") : ""
                                };
                                ssImgs1_1 = ssImgs1_1[0].split(",");
                                for (int i = 0; i < ssImgs1_1.length; i++) {
                                    if (ssImgs1_1[i] != null && !ssImgs1_1[i].equals("")) {
                            %>
                            <div  style="position: relative;width: 100px;height:140px;display: inline-block;text-align: center;margin: auto;" id="div_imgstep1_5ss<%=i+1%>">
                                    <img class="imgclass gallery-pic" id="imgstep1_5ss_view<%=i+1%>" name="imgstep1_5ss_view<%=i+1%>" src="<%=ssImgs1_1[i]%>"  style="width: 100%;height:100px;border-radius:10px;">
                            </div>
                            <%
                                    }
                                }
                            %>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <a onclick="erp()" class="btn btn-primary">提交</a>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    function erp() {
        var state_code= $('input[name="state_code"]:checked').val();
        var yxsm= $('#yxsm').val();
        var tr_msg= $('#tr_msg').val();
        var imgstep1_5ss='${erp_result_value.imgstep1_5ss}';
        var icbc_id= '${requestScope.infodb.icbc_id}';
        var type_id= '${requestScope.infodb.type_id}';
        var id= '${requestScope.infodb.id}';
        tr_msg = tr_msg.replace(/\ +/g,"").replace(/[\r\n]/g,"");
        imgstep1_5ss=imgstep1_5ss.replace(/\,/g,"-");
        //alert(icbc_id+"----"+type_id+"---"+id);
        if(!state_code){
            alert("审核结果不能为空!");
            return false;
        }
        $.ajax({
            type: "POST",      //data 传送数据类型。post 传递
            dataType: 'json',  // 返回数据的数据类型json
            url: "/manager/ajaxpost",  // 控制器方法
            data: {
                yxsm : yxsm,
                state_code : state_code,
                tr_msg : tr_msg,
                imgstep1_5ss : imgstep1_5ss,
                icbc_id:icbc_id,
                type_id:type_id,
                id:id,
                type:'3'
            },  //传送的数据
            error: function () {
                alert("编辑失败...请稍后重试！");
            },
            success: function (data) {
                alert(data.msg);
                window.location.href="/manager/index?cn=mytask&sdo=list&type=ddbx";
            }
        });

    }
</script>