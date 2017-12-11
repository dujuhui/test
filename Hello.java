
package net.simpotech.simpo.modules.gxm.web;

import com.zlgx.base.constants.Constants;
import com.zlgx.base.vo.AjaxResponseVo;
import net.simpotech.simpo.common.persistence.Page;
import net.simpotech.simpo.common.utils.StringUtils;
import net.simpotech.simpo.common.vo.MapVo;
import net.simpotech.simpo.modules.gxm.entity.DemandItemVo;
import net.simpotech.simpo.modules.gxm.entity.DemandVo;
import net.simpotech.simpo.modules.gxm.service.DemandService;
import net.simpotech.simpo.modules.sys.entity.User;
import net.simpotech.simpo.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * name
 * @author  dujuhui
 * date    2017/10/30
 * version 1.0
 */
@Controller
@RequestMapping(value = "${adminPath}/gxm/demand")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @ModelAttribute
    public DemandVo get(@RequestParam(required=false) String id) {
        DemandVo entity = null;
        if (StringUtils.isNotBlank(id)){
            entity = demandService.get(id);
        }
        if (entity == null){
            entity = new DemandVo();
        }
        return entity;
    }

    @RequiresPermissions("gxm:demand:view")
    @RequestMapping(value = {"list", ""})
    public String list(DemandVo demandVo, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<DemandVo> page = demandService.findPage(new Page<DemandVo>(request, response), demandVo);
        List<MapVo> servicers = demandService.listServicers();
        model.addAttribute("page", page);
        model.addAttribute("servicers", servicers);
        return "modules/gxm/demandList";
    }

    /**
     * 订单详情入口
     */
    @RequiresPermissions("gxm:demand:form")
    @RequestMapping("detail")
    public String detail(String id, Model model) {
        DemandVo demand = demandService.get(id);
        String fileType = "";
        if(org.apache.commons.lang3.StringUtils.isNotBlank(demand.getDemandFile())){
            fileType = demand.getDemandFile().substring(demand.getDemandFile().lastIndexOf(".")+1);
        }
        model.addAttribute("fileType",fileType);
        model.addAttribute("demand",demand);
        if(Constants.DEMANDBYITEMS.equals(demand.getDemandType())){
            List<DemandItemVo> list = demandService.listDemandItems(id);
            model.addAttribute("list",list);
        }
        return "modules/gxm/demandView";
    }

    @RequestMapping("cancel")
    @ResponseBody
    public AjaxResponseVo cancel( DemandVo demandVo) {
        User user = UserUtils.getUser();
        demandVo.setDisposeMan(user.getName());
        demandVo.setDisposeManId(user.getUserId());
        return demandService.cancelDemand(demandVo);
    }
}
