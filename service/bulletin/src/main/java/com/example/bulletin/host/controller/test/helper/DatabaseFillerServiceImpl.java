package com.example.bulletin.host.controller.test.helper;

import com.example.bulletin.domain.entity.*;
import com.example.bulletin.domain.entity.base.Location;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import com.example.bulletin.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseFillerServiceImpl implements DatabaseFillerService {

    private final UserRepository userRepository;
    private final TradeAccountRepository tradeAccountRepository;
    private final CategoryRepository categoryRepository;
    private final CharacteristicRepository characteristicRepository;
    private final CharacteristicValueRepository characteristicValueRepository;
    private final BulletinRepository bulletinRepository;

    private static final int USER_COUNT = 100;
    private static final int CATEGORY_COUNT = 50;
    private static final int CHARACTERISTICS_PER_CATEGORY = 5;
    private static final int VALUES_PER_CHARACTERISTIC = 4;
    private static final int BULLETIN_COUNT = 10_000;

    private final Random random = ThreadLocalRandom.current();

    private static final List<String> CHARACTERISTIC_NAMES = Arrays.asList(
            "Цвет", "Размер", "Материал", "Вес", "Бренд",
            "Страна производства", "Год выпуска", "Состояние",
            "Гарантия", "Тип", "Мощность", "Объем"
    );

    private static final List<String> COLOR_VALUES = Arrays.asList(
            "Красный", "Синий", "Зеленый", "Черный", "Белый", "Желтый"
    );

    private static final List<String> SIZE_VALUES = Arrays.asList(
            "XS", "S", "M", "L", "XL", "XXL"
    );

    private static final List<String> MATERIAL_VALUES = Arrays.asList(
            "Дерево", "Металл", "Пластик", "Стекло", "Кожа", "Ткань"
    );

    private static final List<String> CONDITION_VALUES = Arrays.asList(
            "Новый", "Как новый", "Хороший", "Средний", "Требует ремонта"
    );

    @Override
    @Transactional
    public void fillDatabase() {
        log.info("🚀 Начало заполнения БД тестовыми данными...");
        long startTime = System.currentTimeMillis();

        try {
            // 1. Создаем пользователей и торговые аккаунты
            List<User> users = createUsers();
            log.info("✅ Создано {} пользователей", users.size());

            // 2. Создаем категории
            List<UUID> categoryIds = createCategoryHierarchy();
            log.info("✅ Создано {} категорий", categoryIds.size());

            // 3. Создаем характеристики для категорий
            Map<UUID, List<Characteristic>> characteristicsByCategoryId =
                    createCharacteristics(categoryIds);
            log.info("✅ Созданы характеристики");

            // 4. Создаем значения для характеристик
            Map<UUID, List<CharacteristicValue>> valuesByCharacteristicId =
                    createCharacteristicValues(characteristicsByCategoryId);
            log.info("✅ Созданы значения для характеристик");

            // 5. Создаем объявления
            createBulletins(users, categoryIds, characteristicsByCategoryId, valuesByCharacteristicId);

            long endTime = System.currentTimeMillis();
            log.info("🎉 Заполнение БД завершено за {} секунд", (endTime - startTime) / 1000.0);

        } catch (Exception e) {
            log.error("❌ Ошибка при заполнении БД", e);
            throw e;
        }
    }

    protected List<User> createUsers() {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < USER_COUNT; i++) {
            User user = User.createUser(
                    UUID.randomUUID(),
                    "user" + i + "@example.com"
            );
            userRepository.saveAndFlush(user);

            createTradeAccountForUser(user, i);
            users.add(user);
        }

        return users;
    }

    private void createTradeAccountForUser(User user, int index) {
        OwnerInfo ownerInfo = new OwnerInfo(user);
        TradeAccount tradeAccount = TradeAccount.createTradeAccount(ownerInfo);

        tradeAccount.setName("Магазин " + index);
        tradeAccount.setPhone("+7" + (900_000_00_00L + index));
        tradeAccount.setContacts("Telegram: @shop" + index);
        tradeAccount.setDescription("Описание магазина " + index);
        tradeAccount.setExactLocation(new Location(
                55.0 + random.nextDouble() * 10,
                37.0 + random.nextDouble() * 10,
                "Город " + (index % 10),
                "Улица " + index
        ));

        if (random.nextInt(100) < 70) {
            tradeAccount.approve();
        }

        tradeAccountRepository.saveAndFlush(tradeAccount);
    }

    protected List<UUID> createCategoryHierarchy() {
        List<UUID> categoryIds = new ArrayList<>();

        for (int i = 0; i < CATEGORY_COUNT / 5; i++) {
            Category root = Category.createRoot("Категория " + i);
            categoryRepository.saveAndFlush(root);
            categoryIds.add(root.getId());

            int childrenCount = 2 + random.nextInt(3);
            for (int j = 0; j < childrenCount; j++) {
                Category child = root.createChild("Подкатегория " + i + "-" + j);
                categoryRepository.saveAndFlush(child);
                categoryIds.add(child.getId());

                int leafCount = 2 + random.nextInt(4);
                for (int k = 0; k < leafCount; k++) {
                    Category leaf = child.createLeafyChild("Лист " + i + "-" + j + "-" + k);
                    categoryRepository.saveAndFlush(leaf);
                    categoryIds.add(leaf.getId());
                }
            }
        }

        return categoryIds;
    }

    protected Map<UUID, List<Characteristic>> createCharacteristics(List<UUID> categoryIds) {
        Map<UUID, List<Characteristic>> result = new HashMap<>();

        for (UUID categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

            if (!category.isLeaf()) continue;

            List<Characteristic> characteristics = new ArrayList<>();
            int charCount = CHARACTERISTICS_PER_CATEGORY + random.nextInt(3);

            for (int i = 0; i < charCount; i++) {
                String name = CHARACTERISTIC_NAMES.get(random.nextInt(CHARACTERISTIC_NAMES.size()));
                if (random.nextBoolean()) {
                    name = name + " " + (i + 1);
                }

                Characteristic characteristic = category.addCharacteristic(name);
                categoryRepository.saveAndFlush(category);
                characteristics.add(characteristic);
            }

            result.put(categoryId, characteristics);
            characteristicRepository.flush();
            categoryRepository.flush();
        }

        return result;
    }

    protected Map<UUID, List<CharacteristicValue>> createCharacteristicValues(
            Map<UUID, List<Characteristic>> characteristicsByCategoryId) {
        Map<UUID, List<CharacteristicValue>> result = new HashMap<>();

        for (List<Characteristic> characteristics : characteristicsByCategoryId.values()) {
            for (Characteristic characteristic : characteristics) {
                Characteristic freshChar = characteristicRepository.findById(characteristic.getId())
                        .orElseThrow(() -> new RuntimeException("Characteristic not found: " + characteristic.getId()));

                List<CharacteristicValue> values = new ArrayList<>();
                int valueCount = VALUES_PER_CHARACTERISTIC + random.nextInt(3);

                for (int i = 0; i < valueCount; i++) {
                    String valueName = generateValueName(freshChar, i);
                    var value = freshChar.addPossibleValue(valueName);
                    characteristicRepository.saveAndFlush(freshChar);
                    values.add(value);
                }

                result.put(characteristic.getId(), values);
            }

            characteristicValueRepository.flush();
        }

        return result;
    }

    private String generateValueName(Characteristic characteristic, int index) {
        String name = characteristic.getName();

        if (name.contains("Цвет")) {
            return COLOR_VALUES.get(random.nextInt(COLOR_VALUES.size()));
        } else if (name.contains("Размер")) {
            return SIZE_VALUES.get(random.nextInt(SIZE_VALUES.size()));
        } else if (name.contains("Материал")) {
            return MATERIAL_VALUES.get(random.nextInt(MATERIAL_VALUES.size()));
        } else if (name.contains("Состояние")) {
            return CONDITION_VALUES.get(random.nextInt(CONDITION_VALUES.size()));
        } else {
            return "Значение " + (index + 1);
        }
    }

    protected void createBulletins(List<User> users, List<UUID> categoryIds,
                                   Map<UUID, List<Characteristic>> characteristicsByCategoryId,
                                   Map<UUID, List<CharacteristicValue>> valuesByCharacteristicId) {

        List<Bulletin> bulletins = new ArrayList<>();
        int batchSize = 50;
        int totalSaved = 0;

        for (int i = 0; i < BULLETIN_COUNT; i++) {
            if (i % 1000 == 0 && i > 0) {
                log.info("Создано {} объявлений...", i);
                bulletinRepository.flush();  // Очищаем контекст
            }

            User user = users.get(random.nextInt(users.size()));
            OwnerInfo ownerInfo = new OwnerInfo(user);
            Bulletin bulletin = Bulletin.createDraft(ownerInfo);

            // Заполняем поля
            bulletin.setTitle(generateTitle());
            bulletin.setDescription(generateDescription());
            bulletin.setPrice(1000 + random.nextInt(99000));
            bulletin.setRating(random.nextDouble() * 5);

            // Выбираем категорию
            UUID categoryId = categoryIds.get(random.nextInt(categoryIds.size()));
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

            if (category.isLeaf()) {
                bulletin.setCategory(category);

                // Добавляем характеристики
                List<Characteristic> availableChars = characteristicsByCategoryId.get(categoryId);
                if (availableChars != null && !availableChars.isEmpty()) {
                    int characteristicsCount = 1 + random.nextInt(Math.min(4, availableChars.size()));
                    addCharacteristicsToBulletin(bulletin, availableChars,
                            valuesByCharacteristicId, characteristicsCount);
                }
            }

            // Добавляем изображения
            int imagesCount = 1 + random.nextInt(3);
            addImagesToBulletin(bulletin, imagesCount);

            // Устанавливаем состояние
            bulletin.setState(random.nextInt(100) < 70 ?
                    BulletinState.PUBLISHED : BulletinState.MODIFIABLE);

            bulletins.add(bulletin);

            if (bulletins.size() >= batchSize) {
                bulletinRepository.saveAllAndFlush(bulletins);  // Используем saveAllAndFlush
                totalSaved += bulletins.size();
                bulletins.clear();
            }
        }

        if (!bulletins.isEmpty()) {
            bulletinRepository.saveAllAndFlush(bulletins);
            totalSaved += bulletins.size();
        }

        log.info("✅ Создано {} объявлений", totalSaved);
    }

    private void addCharacteristicsToBulletin(Bulletin bulletin,
                                              List<Characteristic> availableChars,
                                              Map<UUID, List<CharacteristicValue>> valuesByCharacteristicId,
                                              int count) {

        List<UUID> characteristicIds = availableChars.stream()
                .map(Characteristic::getId)
                .collect(Collectors.toList());

        Collections.shuffle(characteristicIds);

        for (int i = 0; i < Math.min(count, characteristicIds.size()); i++) {
            UUID characteristicId = characteristicIds.get(i);

            Characteristic characteristic = characteristicRepository.findById(characteristicId)
                    .orElse(null);

            if (characteristic == null) continue;

            List<CharacteristicValue> values = valuesByCharacteristicId.get(characteristicId);
            if (values != null && !values.isEmpty()) {
                CharacteristicValue valueTemplate = values.get(random.nextInt(values.size()));
                CharacteristicValue value = characteristicValueRepository.findById(valueTemplate.getId())
                        .orElse(null);

                if (value != null) {
                    try {
                        BulletinCharacteristic bc = bulletin.addCharacteristic(characteristic);
                        bc.setValue(value);
                    } catch (Exception e) {
                        log.warn("Не удалось добавить характеристику: {}", e.getMessage());
                    }
                }
            }
        }
    }

    private void addImagesToBulletin(Bulletin bulletin, int count) {
        for (int i = 0; i < count; i++) {
            UUID imageId = UUID.randomUUID();
            BulletinImage image = bulletin.addImage(imageId);
            if (i == 0) {
                image.setMain();
            }
        }
    }

    private String generateTitle() {
        String[] adjectives = {"Новый", "Отличный", "Красивый", "Удобный", "Стильный", "Модный", "Качественный"};
        String[] nouns = {"телефон", "ноутбук", "велосипед", "книга", "стол", "стул", "диван", "холодильник"};

        return adjectives[random.nextInt(adjectives.length)] + " " +
                nouns[random.nextInt(nouns.length)] + " " +
                (random.nextInt(24) + 2000);
    }

    private String generateDescription() {
        String[] descriptions = {
                "В отличном состоянии. Полностью рабочий.",
                "Продаю за ненадобностью. Торг уместен.",
                "Новый в упаковке. Гарантия.",
                "Пользовался аккуратно. Есть небольшие следы эксплуатации.",
                "Срочная продажа. Отличная цена."
        };

        return descriptions[random.nextInt(descriptions.length)] + " " +
                "Подробности по телефону. Возможна доставка.";
    }
}
