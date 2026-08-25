WITH seed AS (
    SELECT
        gs,

        -- EN: Creates a repeatable first-name distribution.
        -- TR: Tekrarlanabilir bir isim dağılımı oluşturur.
        (ARRAY[
             'Büşra', 'Elif', 'Zeynep', 'Ece', 'Selin',
         'Cem', 'Mert', 'Emre', 'Can', 'Kerem',
         'Ayşe', 'Deniz', 'Burak', 'Melis', 'Onur'
             ])[1 + ((gs - 1) % 15)] AS first_name,

     -- EN: Creates a repeatable last-name distribution.
     -- TR: Tekrarlanabilir bir soyisim dağılımı oluşturur.
    (ARRAY[
    'İshakoğlu', 'Yılmaz', 'Aydın', 'Demir', 'Kaya',
    'Arslan', 'Koç', 'Şahin', 'Çelik', 'Öztürk',
    'Aksoy', 'Kurt', 'Yıldız', 'Eren', 'Doğan'
    ])[1 + (((gs - 1) / 15) % 15)] AS last_name

FROM generate_series(1, 1000) AS gs
    )

INSERT INTO customers (
    first_name,
    last_name,
    email,
    phone,
    created_at,
    updated_at
)
SELECT
    /*
     * EN:
     * Some records intentionally contain casing or whitespace variations
     * for future data-quality analysis.
     *
     * TR:
     * Bazı kayıtlar ileride veri kalitesi analizi için bilinçli olarak
     * büyük/küçük harf veya boşluk farklılıkları içerir.
     */
    CASE
        WHEN gs % 25 = 0 THEN UPPER(first_name)
        WHEN gs % 40 = 0 THEN ' ' || first_name || ' '
        ELSE first_name
        END,

    CASE
        WHEN gs % 30 = 0 THEN UPPER(last_name)
        WHEN gs % 45 = 0 THEN ' ' || last_name || ' '
        ELSE last_name
        END,

    /*
     * EN:
     * Email addresses are always unique because customers.email
     * currently has a UNIQUE constraint.
     *
     * TR:
     * customers.email alanında UNIQUE constraint olduğu için
     * email adresleri benzersiz üretilir.
     */
    LOWER(first_name)
        || '.'
        || LOWER(last_name)
        || gs
        || '@'
        || CASE
               WHEN gs % 10 < 5 THEN 'gmail.com'
               WHEN gs % 10 < 7 THEN 'hotmail.com'
               WHEN gs % 10 < 9 THEN 'outlook.com'
               ELSE 'nexoracorp.com'
        END,

    /*
     * EN:
     * Introduces controlled phone-quality scenarios:
     * - null values
     * - international format
     * - formatted local numbers
     * - standard local numbers
     *
     * TR:
     * Kontrollü telefon veri kalitesi senaryoları üretir:
     * - null değer
     * - uluslararası format
     * - formatlı yerel numara
     * - standart yerel numara
     */
    CASE
        WHEN gs % 20 = 0 THEN NULL

        WHEN gs % 13 = 0 THEN
            '+90 5'
                || LPAD(((300000000 + gs) % 1000000000)::TEXT, 9, '0')

        WHEN gs % 17 = 0 THEN
            '0 (5'
                || SUBSTRING(
                    LPAD(((300000000 + gs) % 1000000000)::TEXT, 9, '0'),
                    1,
                    2
                   )
                || ') '
                || SUBSTRING(
                    LPAD(((300000000 + gs) % 1000000000)::TEXT, 9, '0'),
                    3,
                    3
                   )
                || ' '
                || SUBSTRING(
                    LPAD(((300000000 + gs) % 1000000000)::TEXT, 9, '0'),
                    6
                   )

        ELSE
            '05'
                || LPAD(((300000000 + gs) % 1000000000)::TEXT, 9, '0')
        END,

    /*
     * EN:
     * Spreads creation dates across roughly two years.
     *
     * TR:
     * Oluşturulma tarihlerini yaklaşık iki yıllık döneme dağıtır.
     */
    CURRENT_TIMESTAMP
        - ((gs * 17) % 730) * INTERVAL '1 day'
    - ((gs * 7) % 24) * INTERVAL '1 hour',

    /*
     * EN:
     * updated_at is always on or after created_at.
     *
     * TR:
     * updated_at değeri her zaman created_at ile aynı veya sonrasındadır.
     */
    CURRENT_TIMESTAMP
    - ((gs * 11) % 365) * INTERVAL '1 day'

FROM seed;