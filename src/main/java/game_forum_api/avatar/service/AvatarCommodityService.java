package game_forum_api.avatar.service;

import game_forum_api.avatar.dto.AvatarCommodityRequest;
import game_forum_api.avatar.model.AvatarCommodity;
import game_forum_api.avatar.repository.AvatarCommodityRepository;
import game_forum_api.exception.common.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvatarCommodityService {

    @Autowired
    private AvatarCommodityRepository avatarCommodityRepository;

    public AvatarCommodity uploadCommodity(AvatarCommodityRequest request) {
        AvatarCommodity commodity = new AvatarCommodity();
        commodity.setCommodityName(request.getCommodityName());
        commodity.setType(request.getType());
        commodity.setPhotoPath(request.getPhotoPath());
        commodity.setShelfTime(request.getShelfTime());
        commodity.setPoint(request.getPoint());
        return avatarCommodityRepository.save(commodity);
    }

    public String deleteCommodity(Integer id) {
        avatarCommodityRepository.deleteById(id);
        return "商品下架成功";
    }

    public List<AvatarCommodity> getAllCommodities() {
        return avatarCommodityRepository.findAll();
    }
    
    public String getCommodityNameById(Integer id) {
    	String commodityName = avatarCommodityRepository.findCommodityNameById(id);
    	
        if (commodityName == null || commodityName.trim().isEmpty()) {
            throw new ResourceNotFoundException("商品ID： " + id + " 不存在");
        }

        return commodityName;
    }
    
    public Integer getPointById(Integer id) {
    	Integer point = avatarCommodityRepository.findPointById(id);
    	
        if (point == null) {
            throw new ResourceNotFoundException("商品ID： " + id + " 不存在");
        }

        return point;
    }
}