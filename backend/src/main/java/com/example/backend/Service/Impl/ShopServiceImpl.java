package com.example.backend.Service.Impl;

import com.example.backend.POJO.Shop;
import com.example.backend.POJO.ShopData;
import com.example.backend.Service.EmployeeService;
import com.example.backend.Service.FlowService;
import com.example.backend.Service.ShopService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.ShopDataMapper;
import com.example.backend.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopDataMapper shopDataMapper;

    @Autowired
    private FlowService flowService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ResultVO<Object> addShop(String name, String address, double size, long manager) {

        Shop shop = new Shop(null, name, address, size, manager);
        try {
            shopMapper.insert(shop);
            flowService.generateFlow(shop.getId(), new Date(), 30);
            shopDataMapper.insert(new ShopData(null, shop.getId(), manager, new ShopData.Data()));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO<>(-1, "添加门店失败", null);
        }
        return new ResultVO<>(0, "添加门店成功", shop);

    }

    @Override
    public ResultVO<Object> deleteShop(long id) {
        if (!employeeService.deleteEmployeeByShop(id)) {
            return new ResultVO<>(-1, "删除门店失败", null);
        }
        try {
            shopMapper.deleteById(id);
            Map<String, Object> searchingMap = new HashMap<>();
            searchingMap.put("shopId", id);
            shopDataMapper.deleteByMap(searchingMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO<>(-1, "删除门店失败", null);
        }
        return new ResultVO<>(0, "删除门店成功", null);
    }

    @Override
    public ResultVO<Object> getShop(long id) {

        try {
            return new ResultVO<>(-1, "获取门店信息成功", shopMapper.selectById(id));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取门店信息失败", null);
        }

    }

    @Override
    public ResultVO<Object> getAllShop(long managerId) {

        try {
            Map<String, Object> searchingMap = new HashMap<>();
            searchingMap.put("manager", managerId);

            List<Shop> shops = shopMapper.selectByMap(searchingMap);
            return new ResultVO<>(0, "获取所有门店信息成功", shops);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取所有门店信息失败", null);
        }

    }

    @Override
    public ResultVO<Object> updateShop(long id, String name, String address, Double size, Long manager) {

        Shop shop = new Shop(id, name, address, size, manager);
        try {
            shopMapper.updateById(shop);
        } catch (Exception e) {
            return new ResultVO<>(-1, "更新门店失败", null);
        }
        return new ResultVO<>(0, "更新门店成功", shopMapper.selectById(id));
    }

    @Override
    public ResultVO<Object> getShopData(long shopId) {
        try {
            Map<String, Object> searchingMap = new HashMap<>();
            searchingMap.put("shopId", shopId);
            return new ResultVO<>(0, "获取门店数据成功", shopDataMapper.selectByMap(searchingMap).get(0));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取门店数据失败", null);
        }
    }

}
