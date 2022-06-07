

const boldIfNotSection = (code) => {
    let num = parseInt(code);
    if (isNaN(num)) {
        throw new Error("param should be a number");
    }
    else if (num % 100 !== 0) {
        return code;
    }
    else {
        return (
          <div style={{fontWeight: "bold"}}>{code}</div>
        )
    }
}

const fraction_w_percent = (num, denom) => {
    if ((num === null) || (denom === null)) {
        if (denom !== null) {
            return denom;
        }
        return '';
    }
    let percent = (parseInt(num) / parseInt(denom)) * 100;
    percent = percent.toFixed();
    return `${num}/${denom} (${percent}%)`;
}

export {
    boldIfNotSection,
    fraction_w_percent
};
