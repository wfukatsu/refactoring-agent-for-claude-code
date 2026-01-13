import { useEffect, useState } from "react";
import { BASE_URL } from "../../utils/constants";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import { useTranslation } from "react-i18next";

export default function Validating({ item }) {
  const axiosPrivate = useAxiosPrivate();
  const [isLoading, setIsLoading] = useState(false);
  const [temparedStatus, setTemparedStatus] = useState(null);

  const { itemId } = { ...item };
  console.log("ENTERED  :: Validating", itemId);

  const { t, i18n } = useTranslation();

  useEffect(() => {
    const fetchValidation = async () => {
      setIsLoading(true);
      await axiosPrivate
        .get(`${BASE_URL}/box/file/checkTamperingStatus/${itemId}`)
        .then((response) => {
          if (response.data.status) {
            const { tamperingStatus } = { ...response.data.data };
            setTemparedStatus(tamperingStatus);
          } else {
            throw Error("");
          }
        })
        .catch((error) => {
          console.error("/////////ERROR :: ", error);
        })
        .finally(() => {
          setIsLoading(false);
        });
    };
    fetchValidation();

    return () => {};
  }, [itemId]);

  return (
    <div>
      {isLoading ? (
        <>
          <h1 className="text-md font-bold p-1">Validating ...</h1>
        </>
      ) : (
        <div style={{paddingLeft:"5px"}}>
          <h1 className="text-md font-bold p-1">
            {temparedStatus && (
              <>
                <span>{t("viewItemsUnderAuditScreenSubFileStatus")} :</span>{" "}
                {temparedStatus}
              </>
            )}
          </h1>
        </div>
      )}
    </div>
  );
}
