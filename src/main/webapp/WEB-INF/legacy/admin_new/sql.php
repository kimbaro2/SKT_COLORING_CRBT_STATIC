<?

# argument1		: sql variable
# argument2~	: string segment	
# return 		: sql 
function createSql()
{
	$nArgs = func_num_args();

	$args = func_get_args();
 	for ($i = 1; $i < $nArgs; $i++) {
		$args[0] = preg_replace("/\?/", $args[$i], $args[0], 1);	
 	}
	return $args[0];
}

$SQL_STATIC_DATA_QUERY = "
      SELECT
         cpid as CPID,
         sum(wap_n), sum(wap_l), sum(wap_p),
         sum(web_n), sum(web_l), sum(web_p),
         sum(ars_n), sum(ars_l), sum(ars_p),
         sum(pda_n), sum(pda_l), sum(pda_p),
         sum(code_n), sum(code_l), sum(code_p),
         sum(copy_n), sum(copy_l), sum(copy_p),
         sum(info_n), sum(info_l), sum(info_p),
         sum(portal_n), sum(portal_l), sum(portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n),
         sum(wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l),
         sum(wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n + wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l + wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p) as TOTAL,
         sum(wap_n + wap_l + wap_p) as WAP,
         sum(web_n + web_l + web_p) as WEB,
         sum(ars_n + ars_l + ars_p) as ARS,
         sum(pda_n + pda_l + pda_p) as PDA,
         sum(code_n + code_l + code_p) as CODE,
         sum(copy_n + copy_l + copy_p) as COPY,
         sum(info_n + info_l + info_p) as INFO,
         sum(portal_n + portal_l + portal_p) as PORTAL
      FROM
      	? 
      WHERE
         timezone between '?' and '?' and ? 
      GROUP BY cpid
      ORDER BY ?";


$SQL_STATIC_RANK_QUERY = "
      SELECT
         cpid,
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n + wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l + wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p) as TOTAL
      FROM
         ?  
      WHERE
         timezone between '?' and '?' and ? 
      GROUP BY cpid
      ORDER BY TOTAL desc";


$SQL_STATIC_TOTAL_QUERY = "
      SELECT
         sum(wap_n), sum(wap_l), sum(wap_p),
         sum(web_n), sum(web_l), sum(web_p),
         sum(ars_n), sum(ars_l), sum(ars_p),
         sum(pda_n), sum(pda_l), sum(pda_p),
         sum(code_n), sum(code_l), sum(code_p),
         sum(copy_n), sum(copy_l), sum(copy_p),
         sum(info_n), sum(info_l), sum(info_p),
         sum(portal_n), sum(portal_l), sum(portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n),
         sum(wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l),
         sum(wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n + wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l + wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p) as TOTAL    
      FROM
         ?   
      WHERE
         timezone between '?' and '?' and ?";

$SQL_STATIC_DATA_QUERY_T = "
      SELECT
         cpid as CPID,
         sum(wap_n), sum(wap_4), sum(wap_l), sum(wap_9), sum(wap_p),
         sum(web_n), sum(web_4), sum(web_l), sum(web_9), sum(web_p),
         sum(ars_n), sum(ars_4), sum(ars_l), sum(ars_9), sum(ars_p),
         sum(pda_n), sum(pda_4), sum(pda_l), sum(pda_9), sum(pda_p),
         sum(code_n), sum(code_4), sum(code_l), sum(code_9), sum(code_p),
         sum(copy_n), sum(copy_4), sum(copy_l), sum(copy_9), sum(copy_p),
         sum(info_n), sum(info_4), sum(info_l), sum(info_9), sum(info_p),
         sum(portal_n), sum(portal_4), sum(portal_l), sum(portal_9), sum(portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n),
         sum(wap_4 + web_4 + ars_4 + pda_4 + code_4 + copy_4 + info_4 + portal_4),
         sum(wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l),
         sum(wap_9 + web_9 + ars_9 + pda_9 + code_9 + copy_9 + info_9 + portal_9),
         sum(wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n + wap_4 + web_4 + ars_4 + pda_4 + code_4 + copy_4 + info_4 + portal_4 + wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l + wap_9 + web_9 + ars_9 + pda_9 + code_9 + copy_9 + info_9 + portal_9 + wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p) as TOTAL,
         sum(wap_n + wap_4 + wap_l + wap_9 + wap_p) as WAP,
         sum(web_n + web_4 + web_l + web_9 + web_p) as WEB,
         sum(ars_n + ars_4 + ars_l + ars_9 + ars_p) as ARS,
         sum(pda_n + pda_4 + pda_l + pda_9 + pda_p) as PDA,
         sum(code_n + code_4 + code_l + code_9 + code_p) as CODE,
         sum(copy_n + copy_4 + copy_l + copy_9 + copy_p) as COPY,
         sum(info_n + info_4 + info_l + info_9 + info_p) as INFO,
         sum(portal_n + portal_4 + portal_l + portal_9 + portal_p) as PORTAL
      FROM
      	? 
      WHERE
         timezone between '?' and '?' and ? 
      GROUP BY cpid
      ORDER BY ?";


$SQL_STATIC_RANK_QUERY_T = "
      SELECT
         cpid,
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n + wap_4 + web_4 + ars_4 + pda_4 + code_4 + copy_4 + info_4 + portal_4 + wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l + wap_9 + web_9 + ars_9 + pda_9 + code_9 + copy_9 + info_9 + portal_9 + wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p) as TOTAL
      FROM
         ?  
      WHERE
         timezone between '?' and '?' and ? 
      GROUP BY cpid
      ORDER BY TOTAL desc";


$SQL_STATIC_TOTAL_QUERY_T = "
      SELECT
         sum(wap_n), sum(wap_4), sum(wap_l), sum(wap_9), sum(wap_p),
         sum(web_n), sum(web_4), sum(web_l), sum(web_9), sum(web_p),
         sum(ars_n), sum(ars_4), sum(ars_l), sum(ars_9), sum(ars_p),
         sum(pda_n), sum(pda_4), sum(pda_l), sum(pda_9), sum(pda_p),
         sum(code_n), sum(code_4), sum(code_l), sum(code_9), sum(code_p),
         sum(copy_n), sum(copy_4), sum(copy_l), sum(copy_9), sum(copy_p),
         sum(info_n), sum(info_4), sum(info_l), sum(info_9), sum(info_p),
         sum(portal_n), sum(portal_4), sum(portal_l), sum(portal_9), sum(portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n),
         sum(wap_4 + web_4 + ars_4 + pda_4 + code_4 + copy_4 + info_4 + portal_4),
         sum(wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l),
         sum(wap_9 + web_9 + ars_9 + pda_9 + code_9 + copy_9 + info_9 + portal_9),
         sum(wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p),
         sum(wap_n + web_n + ars_n + pda_n + code_n + copy_n + info_n + portal_n + wap_4 + web_4 + ars_4 + pda_4 + code_4 + copy_4 + info_4 + portal_4 + wap_l + web_l + ars_l + pda_l + code_l + copy_l + info_l + portal_l + wap_9 + web_9 + ars_9 + pda_9 + code_9 + copy_9 + info_9 + portal_9 + wap_p + web_p + ars_p + pda_p + code_p + copy_p + info_p + portal_p) as TOTAL
      FROM
         ?   
      WHERE
         timezone between '?' and '?' and ?";


$SQL_STATIC_VIEW_CHANNEL = "
		SELECT
			DESCRIPTION,
			sum(wap_n + wap_l + wap_p) as WAP,
			sum(web_n + web_l + web_p) as WEB,
			sum(ars_n + ars_l + ars_p) as ARS,
			sum(pda_n + pda_l + pda_p) as PDA,
			sum(code_n + code_l + code_p) as CODE,
			sum(copy_n + copy_l + copy_p) as COPY,
			sum(info_n + info_l + info_p) as INFO,
			sum(portal_n + portal_l + portal_p) as PORTAL
		FROM
			? A, cp_id B 
		WHERE
			A.cpid=B.CP_ID and 
			timezone between '?' and '?' and ? 
		GROUP BY cpid
		ORDER BY cpid";


$SQL_STATIC_VIEW_CHANNEL_SUM = "
		SELECT
			sum(wap_n + wap_l + wap_p) as WAP,
			sum(web_n + web_l + web_p) as WEB,
			sum(ars_n + ars_l + ars_p) as ARS,
			sum(pda_n + pda_l + pda_p) as PDA,
			sum(code_n + code_l + code_p) as CODE,
			sum(copy_n + copy_l + copy_p) as COPY,
			sum(info_n + info_l + info_p) as INFO,
			sum(portal_n + portal_l + portal_p) as PORTAL
		FROM
			? 
		WHERE
			timezone between '?' and '?' and ?"; 


$SQL_STATIC_VIEW_SERVICE = "
		SELECT
			B.DESCRIPTION,
			sum(A.wap_n + A.wap_l + A.wap_p) as WAP,
			sum(A.web_n + A.web_l + A.web_p) as WEB,
			sum(A.code_n + A.code_l + A.code_p + A.portal_n + A.portal_l + A.portal_p) as POTAL,
			sum(A.ars_n + A.ars_l + A.ars_p + A.pda_n + A.pda_l + A.pda_p + A.copy_n + A.copy_l + A.copy_p + A.info_n + A.info_l + A.info_p) as OTH
		FROM
			? A, cp_id B
		WHERE
			A.cpid=B.CP_ID and 
			A.timezone between '?' and '?' and ? 
		GROUP BY A.cpid
		ORDER BY A.cpid";


$SQL_STATIC_VIEW_SERVICE_SUM = "
		SELECT
			sum(wap_n + wap_l + wap_p) as WAP,
			sum(web_n + web_l + web_p) as WEB,
			sum(code_n + code_l + code_p + portal_n + portal_l + portal_p) as POTAL,
			sum(ars_n + ars_l + ars_p + pda_n + pda_l + pda_p + copy_n + copy_l + copy_p + info_n + info_l + info_p) as OTH
		FROM
			? 
		WHERE
			timezone between '?' and '?' and ? ";

?>

