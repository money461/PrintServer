package com.bondex.util.sql;


public class Sqls {
	
	

    private static final String[] COUNT_KEY_WORD = { " group ", " order ", " having ", " limit ", " procedure ", " for ", " lock " };

    public static String substringCountBeforeKeyWord(String str) {
        return Strings.substringBeforeIgnoreCases(str, COUNT_KEY_WORD);
    }

    /*
     * 如果sql中包含[group by]，则要移除[group by]后面的语句并只取[group by]的字段。
     * 截取规则: mysql select 语句格式
     * SELECT [STRAIGHT_JOIN] [SQL_SMALL_RESULT] [SQL_BIG_RESULT] [HIGH_PRIORITY]   
     * [DISTINCT | DISTINCTROW | ALL]   
     * select_expression，...   
     * [INTO {OUTFILE | DUMPFILE} ’file_name’ export_options]   
     * [FROM table_references   ][WHERE where_definition]   [GROUP BY col_name，...]   [HAVING where_definition]   
     * [ORDER BY {unsigned_integer | col_name | formula} ][ASC | DESC] ，...]   
     * [LIMIT ][offset，] rows]   [PROCEDURE procedure_name] ] 
     * [FOR UPDATE | LOCK IN SHARE MODE]]
     */
    public static String buildCountSql(String sql) {
        // sql:select * from edu_pay_fee_order where 1=1 group by org_name order by id DESC;
        StringBuilder countSql = new StringBuilder();
        String alias = "DATABASE_TABLE_ALIAS"; // 最终表别名
        String query = "1";
        String appendGroup = " ";

        String lsql = sql;
        if (Strings.isContains(sql, ")")) {
        }
        lsql = Strings.substringAfterLast(sql, ")");// 获取最有一个括号后面的字符串
        if (Strings.isContainsIgnoreCase(lsql, "group")) {
            query = Strings.substringAfterIgnoreCases(lsql, " group by ");
            query = substringCountBeforeKeyWord(query);
            if (Strings.isContains(query, ".")) {
                query = Strings.substringAfterLast(query, ".");
                query = alias + "." + query;
            }
            query = "DISTINCT " + query + " ";
            // sql中如果是where in .... group
            if (Strings.isContainsIgnoreCase(query, ")")) {
                query = Strings.remove(query, ")");
                appendGroup = " ) ";
            }
            // query:distinct org_name;
        }
        // 取[group|order]前面的
        // sql:select * from edu_pay_fee_order where 1=1

        // sql = substringCountBeforeKeyWord(sql);
        // // 取[from]后面的
        // // sql:from edu_pay_fee_order where 1=1
        // sql = Strings.substringAfterAndContainsIgnoreCases(sql, " from ");
        // // 组合
        // // countSql: SELECT COUNT(distinct org_name ) from edu_pay_fee_order where 1=1
        // // countSql: SELECT COUNT(*) from edu_pay_fee_order where 1=1

        sql = " FROM (" + sql + ") AS " + alias;
        countSql.append("SELECT COUNT(" + query + ") " + sql + appendGroup);
        return countSql.toString();
    }

    public static String buildPageSql(String sql, int pageNumber, int pageSize) {
        StringBuilder pageSQL = new StringBuilder();
        if (pageNumber < 1) {
            pageNumber = Strings.PAGE_NUMBER;
        }
        if (pageSize < 1) {
            pageSize = Strings.PAGE_SIZE;
        }
        int offset = pageSize * (pageNumber - 1);
        pageSQL.append(sql);
        pageSQL.append(" limit ").append(offset).append(", ").append(pageSize);   // limit can use one or two '?' to pass paras
        return pageSQL.toString();
    }

    // public static String bulidSearch(Map<String, ?> filter, Map<String, ?> like) {
    // String sql = "";
    // return sql;
    // }

    // public static String bulidOrder(Map<String, String> sort) {
    // String sql = "";
    // Sort sorts = buildSortFilter(sort);
    // if (sorts != null) {
    // StringBuffer order_sql = new StringBuffer(" ORDER BY ");
    // order_sql.append(Strings.remove(sorts.toString(), ":"));
    // sql += order_sql.toString();
    // }
    // return sql;
    // }

}
