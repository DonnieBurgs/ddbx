package com.example.ddbx.tt.table;

import com.example.ddbx.tt.data.TtList;
import com.example.ddbx.tt.data.TtMap;
import com.example.ddbx.tt.tool.Config;
import com.example.ddbx.tt.tool.DbCtrl;
import com.example.ddbx.tt.tool.Tools;

import javax.servlet.http.HttpServletRequest;

public class loan_jqcl extends DbCtrl {

    private final String title = "结清处理";
    private String orderString = "ORDER BY dt_edit DESC"; // 默认排序
    private boolean canDel = false;
    private boolean canAdd = false;
    private final String classAgpId = "153"; // 随便填的，正式使用时应该跟model里此模块的ID相对应
    public boolean agpOK = false;// 默认无权限

    public loan_jqcl(){
        super("loan_overdue_list");

        AdminAgp adminAgp = new AdminAgp();
        try {
            if (adminAgp.checkAgp(classAgpId)) { // 如果有权限
                Config.log.info("权限检查成功！");
                agpOK = true;
            } else {
                errorCode = 444;
                errorMsg = "您好，您暂无权限！";
            }
        } catch (Exception e) {
            Tools.logError(e.getMessage(), true, false);
        } finally {
            adminAgp.closeConn();
        }
    }

    @Override
    public void doGetList(HttpServletRequest request, TtMap post) {
        System.out.println("查询list!!!!!!!!!");
        if (!agpOK) {// 演示在需要权限检查的地方插入权限标志判断
            request.setAttribute("errorMsg", errorMsg);
            return;
        }
        String kw = ""; // 搜索关键字
        String dtbe = ""; // 搜索日期选择
        int pageInt = Integer.valueOf(Tools.myIsNull(post.get("p")) == false ? post.get("p") : "1"); // 当前页
        int limtInt = Integer.valueOf(Tools.myIsNull(post.get("l")) == false ? post.get("l") : "10"); // 每页显示多少数据量
        String whereString = "true";
        String tmpWhere = "";
        System.out.println("............."+post.get("hxtype"));
        if ("1".equals(post.get("jqtype"))){     //正常结清
            tmpWhere = " and t.type_id = 6 and t.type_status = 61";
        }
        if ("2".equals(post.get("jqtype"))){     //提前结清
            tmpWhere = " and t.type_id = 6 and t.type_status = 62";
        }
        if ("3".equals(post.get("jqtype"))){     //强制结清
            tmpWhere = " and t.type_id = 6 and t.type_status = 63";
        }
        if ("4".equals(post.get("jqtype"))){     //亏损结清
            tmpWhere = " and t.type_id = 6 and t.type_status = 64";
        }


        String fieldsString = "t.*, c.order_code, c.c_name, c.c_cardno, b.`name` bank_name, ca.car_type, ca.carno, f.`name` fs_name, g.`name` gems_name"; // 显示字段列表如t.id,t.name,t.dt_edit,字段数显示越少加载速度越快，为空显示所有
        TtList list = null;
        /* 开始处理搜索过来的字段 */
        kw = post.get("kw");
        dtbe = post.get("dtbe");

        if (Tools.myIsNull(kw) == false) {
            whereString += " AND c_name like '%" + kw + "%'";
        }
        if (Tools.myIsNull(dtbe) == false) {
            dtbe = dtbe.replace("%2f", "-").replace("+", "");
            String[] dtArr = dtbe.split("-");
            dtArr[0] = dtArr[0].trim();
            dtArr[1] = dtArr[1].trim();
            System.out.println("DTBE开始日期:" + dtArr[0] + "结束日期:" + dtArr[1]);
            // todo处理选择时间段
        }
        /* 搜索过来的字段处理完成 */

        System.out.println("查询list");

        whereString += tmpWhere; // 过滤
        orders = orderString;// 排序
        p = pageInt; // 显示页
        limit = limtInt; // 每页显示记录数
        showall = true; // 忽略deltag和showtag
        leftsql= "LEFT JOIN dd_icbc c on c.id=t.icbc_id " +
                 "LEFT JOIN dd_icbc_cars ca on ca.icbc_id=t.icbc_id " +
                 "LEFT JOIN fs f on f.id=t.gems_fs_id " +
                 "LEFT JOIN icbc_banklist b on b.id=c.bank_id " +
                 "LEFT JOIN admin g on g.id=t.gems_id ";

        list = lists(whereString, fieldsString);
        System.out.println("list::::++  "+list);
        if (!Tools.myIsNull(kw)) { // 搜索关键字高亮
            for (TtMap info : list) {
                info.put("c_name", info.get("c_name").replace(kw, "<font style='color:red;background:#FFCC33;'>" + kw + "</font>"));
            }
        }
        System.out.println("li::::  "+list);
        request.setAttribute("list", list);// 列表list数据
        request.setAttribute("recs", recs); // 总记录数
        String htmlpages = getPage("", 0, false); // 分页html代码,
        request.setAttribute("pages", pages); // 总页数
        request.setAttribute("p", pageInt); // 当前页码
        request.setAttribute("l", limtInt); // limit量
        request.setAttribute("lsitTitleString", title); // 标题
        request.setAttribute("htmlpages", htmlpages); // 分页的html代码
        request.setAttribute("canDel", canDel); // 是否显示删除按钮
        request.setAttribute("canAdd", canAdd); // 是否显示新增按钮
        // request.setAttribute("showmsg", "测试弹出消息提示哈！"); //如果有showmsg字段，在载入列表前会提示
    }


    @Override
    public void doGetForm(HttpServletRequest request, TtMap post) {
        System.out.println("pppp"+post);
        long nid = Tools.myIsNull(post.get("id")) ? 0 : Tools.strToLong(post.get("id"));

        String bbsql = "select * from loan_overdue_list where id = " + nid;
        TtMap bbmap = Tools.recinfo(bbsql);

        String sql = "SELECT SQL_CALC_FOUND_ROWS\n" +
                "\tt.*,\n" +
                "\tk.dk_price,\n" +
                "\tk.dk_total_price,\n" +
                "\tk.aj_date,\n" +
                "\tc.pg_price,\n" +
                "\tb.`name` blankname,\n" +
                "\tc.ppxh,\n" +
                "\tc.car_type,\n" +
                "\tc.car_vin,\n" +
                "\tc.motorcode,\n" +
                "\tc.carno,\n" +
                "\tc.car_color_id,\n" +
                "\tk.aj_lv,\n" +
                "\tk.sf_price,\n" +
                "\tk.jrfw_price,\n" +
                "\tib.`name` bankname\n" +
                "FROM\n" +
                "\tdd_icbc t\n" +
                "\tLEFT JOIN icbc_kk k ON k.icbc_id = t.id\n" +
                "\tLEFT JOIN dd_icbc_cars c ON c.icbc_id = t.id\n" +
                "\tLEFT JOIN icbc_banklist b ON b.id = t.bank_id\n" +
                "\tLEFT JOIN icbc_banklist ib ON ib.id = t.bank_id\n" +
                "WHERE\n" +
                "\tt.id = " + bbmap.get("icbc_id");
        TtMap map = Tools.recinfo(sql);

        String hkjhsql = "SELECT * FROM loan_repayment_schedule WHERE icbc_id = " + bbmap.get("icbc_id");
        TtList reclist = Tools.reclist(hkjhsql);

        //贷后信息
        String dhsql = "select *,a.`name` gems_name,f.`name` fs_name from icbc_kk k left join admin a on a.id=k.gems_id left join fs f on f.id=k.gems_fs_id where icbc_id = " + bbmap.get("icbc_id");
        TtMap mapafter = Tools.recinfo(dhsql);

        //记录栏
        String jlsql = "select lo.*,a.`name` gems_name from loan_overdue_list_result lo left join admin a on a.id = lo.mid_add where icbc_id = " + bbmap.get("icbc_id");
        TtList jllist = Tools.reclist(jlsql);

        System.out.println("jjjjjjj" + jllist);
        System.out.println("主贷人信息:"+map);
        String jsonInfo = Tools.jsonEncode(map);
        request.setAttribute("info", jsonInfo);//info为json后的info
        request.setAttribute("infodb", map);//infodb为TtMap的info
        request.setAttribute("hkjh", reclist);
        request.setAttribute("mapafter", mapafter);
        request.setAttribute("bbmap",bbmap);
        request.setAttribute("jllist", jllist);
        request.setAttribute("jqtype", post.get("jqtype"));
        request.setAttribute("id", nid);
    }

    @Override
    public void doPost(TtMap post, long id, TtMap result2) {
        super.doPost(post, id, result2);
    }
}
