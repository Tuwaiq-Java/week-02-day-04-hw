package com.example.carmanagementsystem.service;

import com.example.carmanagementsystem.model.Car;
import com.example.carmanagementsystem.model.User;
import com.example.carmanagementsystem.model.UserOwnedCar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CarService {
    private ArrayList<Car> cars = new ArrayList<>();
    private final UserService userService;

    public ArrayList<Car> getCars(){
        return cars;
    }

    public boolean addCar(Car car ) {
        return cars.add(car);
    }

    public ArrayList<Car> getCarsByType(String carType) {
        ArrayList<Car> carsByType = new ArrayList<>();
        for(Car car : cars) {
            if (car.getCarType().equals(carType)) {
                carsByType.add(car);
            }
        }
        return carsByType;
    }
    public boolean editCar(Car car) {
        if(checkIfCarExists(car.getCarID()) == null) {
            return false;
        }

        for (int i = 0; i < cars.size(); i++) {
            if(car.getCarID().equals(cars.get(i).getCarID())){
                cars.set(i,car);
                return true;
            }
        }
        return false;
    }

    public Car checkIfCarExists(String carid) {
        for(Car car : cars) {
            if (car.getCarID().equals(carid)) {
                return car;
            }
        }
        return null;
    }

    public Integer purchaseCar(String carid, String userid) {
        Car car = checkIfCarExists(carid);
        if (car == null) {
            return -1;
        }
        User user = userService.checkIfUserExist(userid);
        if (user == null) {
            return 0;
        }
        if(car.getStock() <= 0) {
            return 1;
        }

        if(user.getBalance() < car.getPrice()) {
            return 2;
        }
        if(user.getCarsOwned() == null) {
            user.setBalance(user.getBalance() - car.getPrice());
            ArrayList<UserOwnedCar> initArr= new ArrayList<>();
            initArr.add(new UserOwnedCar(car.getCarID(),car.getCarType(),car.getPrice(),car.getModel()));
            user.setCarsOwned(initArr);
            car.setStock(car.getStock() - 1);
            return 3;
        }
        user.setBalance(user.getBalance() - car.getPrice());
        ArrayList<UserOwnedCar> carsArr= user.getCarsOwned();
        carsArr.add(new UserOwnedCar(car.getCarID(),car.getCarType(),car.getPrice(),car.getModel()));
        user.setCarsOwned(carsArr);
        car.setStock(car.getStock() - 1);
        return 3;
    }

    public Integer resellCar(String carid, String userid) {
        Car car = checkIfCarExists(carid);
        if (car == null) {
            return -1;
        }
        User user = userService.checkIfUserExist(userid);
        if (user == null) {
            return 0;
        }
        if(user.getCarsOwned().size() == 0) {
            return 1;//doesn't have car
        }
        user.setBalance(user.getBalance() + car.getPrice());
        car.setStock(car.getStock() + 1);
        ArrayList<UserOwnedCar> carsArr= user.getCarsOwned();
        carsArr.remove(new UserOwnedCar(car.getCarID(),car.getCarType(),car.getPrice(),car.getModel()));
        user.setCarsOwned(carsArr);
        return 2;
    }

}